package com.dataracy.modules.user.application.service.command.content;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.in.command.command.LogoutUserUseCase;
import com.dataracy.modules.user.application.port.in.command.command.ModifyUserInfoUseCase;
import com.dataracy.modules.user.application.port.in.command.command.WithdrawUserUseCase;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCommandService implements
        ModifyUserInfoUseCase,
        WithdrawUserUseCase,
        LogoutUserUseCase
{
    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;

    private final DuplicateNicknameUseCase duplicateNicknameUseCase;
    private final ValidateAuthorLevelUseCase validateAuthorLevelUseCase;
    private final ValidateOccupationUseCase validateOccupationUseCase;
    private final ValidateVisitSourceUseCase validateVisitSourceUseCase;
    private final ValidateTopicUseCase validateTopicUseCase;
    private final FileCommandUseCase fileCommandUseCase;

    private final JwtValidateUseCase jwtValidateUseCase;
    private final ManageRefreshTokenUseCase manageRefreshTokenUseCase;

    /**
     * 회원의 정보를 수정하고(필수 유효성 검사 수행) 필요 시 프로필 이미지를 업로드하여 갱신한다.
     *
     * <p>요청한 닉네임을 키로 분산락을 획득하여 동시성 충돌을 방지한다. 요청 데이터 유효성 검사(닉네임 중복,
     * 저자 레벨 필수 등)와 선택적 연관 엔터티 검사(직업, 방문경로, 관심 토픽)를 수행한 뒤 사용자 정보를 갱신한다.
     * profileImageFile이 null이 아니고 비어있지 않으면 파일을 저장소에 업로드하고 사용자 프로필 이미지 URL을 업데이트한다.</p>
     *
     * @param userId            수정 대상 사용자 계정의 식별자
     * @param profileImageFile  새 프로필 이미지 파일(없으면 null 또는 비어있는 파일을 전달하여 이미지를 유지)
     * @param requestDto        수정할 사용자 정보 DTO; 닉네임은 분산락 키로 사용됨
     */
    @Override
    @Transactional
    public void modifyUserInfo(Long userId, MultipartFile profileImageFile, ModifyUserInfoRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("ModifyUserInfoUseCase", "회원 정보 수정 서비스 시작 userId=" + userId);

        // 기존 닉네임 조회
        String savedNickname = userQueryPort.findNicknameById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("ModifyUserInfoUseCase", "[회원 정보 수정] 아이디에 해당하는 유저가 존재하지 않습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

        // 닉네임 변경 여부에 따라 다른 분산락 적용
        if (requestDto.nickname().equals(savedNickname)) {
            // 닉네임이 변경되지 않은 경우 - userId 기반 분산락 (동시성 문제 방지)
            modifyUserInfoWithUserIdLock(userId, profileImageFile, requestDto, startTime);
        } else {
            // 닉네임이 변경된 경우 - 닉네임 기반 분산락 (중복 방지)
            modifyUserInfoWithNicknameLock(userId, savedNickname, profileImageFile, requestDto, startTime);
        }
    }

    @DistributedLock(
            key = "'lock:nickname:' + #requestDto.nickname()",
            waitTime = 500L,
            leaseTime = 5000L,
            retry = 3
    )
    @Transactional
    public void modifyUserInfoWithNicknameLock(Long userId, String savedNickname, MultipartFile profileImageFile, ModifyUserInfoRequest requestDto, Instant startTime) {
        // 회원 정보 수정 요청 정보 유효성 검사
        validateModifyUserInfo(
                requestDto.nickname(),
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.visitSourceId(),
                requestDto.topicIds(),
                profileImageFile
        );
        userCommandPort.modifyUserInfo(userId, requestDto);

        // 새로운 프로필 이미지 첨부 시 업데이트, 없을 경우 기존 유지
        modifyProfileImageFile(profileImageFile, userId, "ModifyUserInfoUseCase");

        LoggerFactory.service().logSuccess("ModifyUserInfoUseCase", "회원 정보 수정 서비스 성공 userId=" + userId, startTime);
    }

    @DistributedLock(
            key = "'lock:user:modify:' + #userId",
            waitTime = 500L,
            leaseTime = 5000L,
            retry = 3
    )
    @Transactional
    public void modifyUserInfoWithUserIdLock(Long userId, MultipartFile profileImageFile, ModifyUserInfoRequest requestDto, Instant startTime) {
        // 회원 정보 수정 요청 정보 유효성 검사 (닉네임 중복 검사 제외)
        validateModifyUserInfoWithoutNickname(
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.visitSourceId(),
                requestDto.topicIds(),
                profileImageFile
        );
        userCommandPort.modifyUserInfo(userId, requestDto);

        // 새로운 프로필 이미지 첨부 시 업데이트, 없을 경우 기존 유지
        modifyProfileImageFile(profileImageFile, userId, "ModifyUserInfoUseCase");

        LoggerFactory.service().logSuccess("ModifyUserInfoUseCase", "회원 정보 수정 서비스 성공 userId=" + userId, startTime);
    }

    @Transactional
    public void modifyUserInfoWithoutLock(Long userId, MultipartFile profileImageFile, ModifyUserInfoRequest requestDto, Instant startTime) {
        // 회원 정보 수정 요청 정보 유효성 검사 (닉네임 중복 검사 제외)
        validateModifyUserInfoWithoutNickname(
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.visitSourceId(),
                requestDto.topicIds(),
                profileImageFile
        );
        userCommandPort.modifyUserInfo(userId, requestDto);

        // 새로운 프로필 이미지 첨부 시 업데이트, 없을 경우 기존 유지
        modifyProfileImageFile(profileImageFile, userId, "ModifyUserInfoUseCase");

        LoggerFactory.service().logSuccess("ModifyUserInfoUseCase", "회원 정보 수정 서비스 성공 userId=" + userId, startTime);
    }

    /**
     * 사용자 정보 수정 요청의 입력값들을 검증합니다.
     *
     * 요청된 프로필 이미지, 닉네임, 작성자 유형(필수), 선택적 직업·방문경로 및 토픽 ID들의 유효성을 확인합니다.
     *
     * @param nickname 수정할 닉네임(중복 검사 대상)
     * @param authorLevelId 필수인 작성자 유형 ID
     * @param occupationId 선택적 직업 ID(널이면 검사하지 않음)
     * @param visitSourceId 선택적 방문경로 ID(널이면 검사하지 않음)
     * @param topicIds 선택적 토픽 ID 목록(널이거나 비어있지 않은 경우 각 ID 존재 여부를 검사)
     * @param profileImageFile 업로드할 프로필 이미지 파일(널 또는 비어있지 않은 경우 이미지 형식 검사)
     */
    private void validateModifyUserInfo(
            String nickname,
            Long authorLevelId,
            Long occupationId,
            Long visitSourceId,
            List<Long> topicIds,
            MultipartFile profileImageFile
    ) {
        // 프로필 이미지 파일 유효성 검사
        FileUtil.validateImageFile(profileImageFile);

        duplicateNicknameUseCase.validateDuplicatedNickname(nickname);

        // 작성자 유형 유효성 검사(필수 컬럼)
        validateAuthorLevelUseCase.validateAuthorLevel(authorLevelId);
        // 직업 유효성 검사(선택 컬럼)
        if (occupationId != null) {
            validateOccupationUseCase.validateOccupation(occupationId);
        }
        // 방문 경로 유효성 검사(선택 컬럼)
        if (visitSourceId != null) {
            validateVisitSourceUseCase.validateVisitSource(visitSourceId);
        }
        // 토픽 id를 통해 토픽 존재 유효성 검사를 시행한다.
        if (topicIds != null && !topicIds.isEmpty()) {
            for (Long topicId : topicIds) {
                validateTopicUseCase.validateTopic(topicId);
            }
        }
    }

    /**
     * 닉네임 중복 검사 없이 사용자 정보 수정 요청의 입력값들을 검증합니다.
     *
     * @param authorLevelId 필수 작성자 유형 ID
     * @param occupationId 선택적 직업 ID(널이면 검사하지 않음)
     * @param visitSourceId 선택적 방문경로 ID(널이면 검사하지 않음)
     * @param topicIds 선택적 토픽 ID 목록(널이거나 비어있지 않은 경우 각 ID 존재 여부를 검사)
     * @param profileImageFile 업로드할 프로필 이미지 파일(널 또는 비어있지 않은 경우 이미지 형식 검사)
     */
    private void validateModifyUserInfoWithoutNickname(
            Long authorLevelId,
            Long occupationId,
            Long visitSourceId,
            List<Long> topicIds,
            MultipartFile profileImageFile
    ) {
        // 프로필 이미지 파일 유효성 검사
        FileUtil.validateImageFile(profileImageFile);

        // 작성자 유형 유효성 검사(필수 컬럼)
        validateAuthorLevelUseCase.validateAuthorLevel(authorLevelId);
        // 직업 유효성 검사(선택 컬럼)
        if (occupationId != null) {
            validateOccupationUseCase.validateOccupation(occupationId);
        }
        // 방문 경로 유효성 검사(선택 컬럼)
        if (visitSourceId != null) {
            validateVisitSourceUseCase.validateVisitSource(visitSourceId);
        }
        // 토픽 id를 통해 토픽 존재 유효성 검사를 시행한다.
        if (topicIds != null && !topicIds.isEmpty()) {
            for (Long topicId : topicIds) {
                validateTopicUseCase.validateTopic(topicId);
            }
        }
    }

    /**
     * 프로필 이미지 파일이 존재하면 S3에 업로드하고 사용자 엔티티의 프로필 이미지 URL을 갱신한다.
     *
     * 파일이 null이거나 비어있으면 아무 동작도 수행하지 않는다.
     *
     * @param profileImageFile 업로드할 프로필 이미지 파일
     * @param userId 이미지가 속한 사용자 ID
     * @param useCase 호출한 유스케이스 이름(예외 발생 시 로그에 사용)
     * @throws RuntimeException 파일 업로드 또는 프로필 URL 갱신 중 오류가 발생하면 발생한다.
     */
    private void modifyProfileImageFile(MultipartFile profileImageFile, Long userId, String useCase) {
        // 프로필 이미지 파일 업로드 시도
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("user", userId, profileImageFile.getOriginalFilename());
                String profileImageFileUrl = fileCommandUseCase.uploadFile(key, profileImageFile);
                userCommandPort.updateProfileImageFile(userId, profileImageFileUrl);
            } catch (Exception e) {
                LoggerFactory.service().logException(useCase, "유저 프로필 이미지 파일 업로드 실패. fileName=" + profileImageFile.getOriginalFilename(), e);
                throw new RuntimeException("유저 프로필 이미지 파일 업로드 실패", e);
            }
        }
    }

    /**
     * 지정된 사용자를 탈퇴(삭제/비활성화) 처리한다.
     *
     * <p>데이터베이스 트랜잭션 내에서 사용자 탈퇴를 수행하며, 내부적으로 사용자 저장소를 통해 탈퇴 상태로 갱신한다.
     *
     * @param userId 탈퇴할 사용자의 식별자
     */
    @Override
    @Transactional
    public void withdrawUser(Long userId) {
        Instant startTime = LoggerFactory.service().logStart("WithdrawUserUseCase", "회원 탈퇴 서비스 시작 userId=" + userId);
        userCommandPort.withdrawalUser(userId);
        LoggerFactory.service().logSuccess("WithdrawUserUseCase", "회원 탈퇴 서비스 성공 userId=" + userId, startTime);
    }

    /**
     * 사용자의 로그아웃을 처리한다.
     *
     * <p>전달된 리프레시 토큰으로 토큰에 포함된 사용자 ID를 검증한 뒤, 일치하면 해당 사용자의 리프레시 토큰을 삭제한다.</p>
     *
     * @param userId       로그아웃을 요청한 사용자의 ID
     * @param refreshToken 클라이언트가 보유한 리프레시 토큰 문자열
     * @throws AuthException 리프레시 토큰이 만료된 경우 또는 토큰에 포함된 사용자 ID가 요청한 사용자 ID와 불일치하는 경우
     */
    @Override
    @Transactional
    public void logout(Long userId, String refreshToken) {
        Instant startTime = LoggerFactory.service().logStart("LogoutUserUseCase", "회원 로그아웃 서비스 시작 userId=" + userId);

        // 쿠키의 리프레시 토큰으로 유저 아이디를 반환한다.
        Long refreshTokenUserId = jwtValidateUseCase.getUserIdFromToken(refreshToken);
        if (refreshTokenUserId == null) {
            LoggerFactory.service().logWarning("LogoutUserUseCase", "[로그아웃] 만료된 리프레시 토큰입니다.");
            throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }
        if (!refreshTokenUserId.equals(userId)) {
            LoggerFactory.service().logWarning("LogoutUserUseCase", "[로그아웃] 인증 요청을 한 유저와 일치하지 않는 리프레시 토큰입니다.");
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
        }

        manageRefreshTokenUseCase.deleteRefreshToken(String.valueOf(userId));
        
        // SecurityContext의 인증 정보 삭제
        SecurityContextProvider.clearSecurityContext();

        // 추후 고보안 서비스일 경우 어세스토큰 블랙리스트 처리까지 생각

        LoggerFactory.service().logSuccess("LogoutUserUseCase", "회원 로그아웃 서비스 성공 userId=" + userId, startTime);
    }
}
