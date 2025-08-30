package com.dataracy.modules.user.application.service.command.command;

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
import com.dataracy.modules.user.application.port.in.command.command.ModifyUserInfoUseCase;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCommandService implements ModifyUserInfoUseCase {
    private final UserCommandPort userCommandPort;

    private final DuplicateNicknameUseCase duplicateNicknameUseCase;

    private final ValidateAuthorLevelUseCase validateAuthorLevelUseCase;
    private final ValidateOccupationUseCase validateOccupationUseCase;
    private final ValidateVisitSourceUseCase validateVisitSourceUseCase;
    private final ValidateTopicUseCase validateTopicUseCase;
    private final FileCommandUseCase fileCommandUseCase;

    @Override
    @DistributedLock(
            key = "'lock:user:modify:nickname:' + #requestDto.nickname()",
            waitTime = 500L,
            leaseTime = 1500L,
            retry = 2
    )
    @Transactional
    public void modifyUserInfo(Long userId, MultipartFile profileImageFile, ModifyUserInfoRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("ModifyUserInfoUseCase", "회원 정보 수정 서비스 시작 nickname=" + requestDto.nickname());

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

        LoggerFactory.service().logSuccess("ModifyUserInfoUseCase", "회원 정보 수정 서비스 성공 nickname=" + requestDto.nickname(), startTime);
    }

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

        // 닉네임 중복 체크
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
}
