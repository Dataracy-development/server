package com.dataracy.modules.user.application.service.query.profile;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.GetVisitSourceLabelFromIdUseCase;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserAuthorLevelIdsUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.application.port.out.query.UserMultiQueryPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService implements
        FindUsernameUseCase,
        FindUserThumbnailUseCase,
        FindUserAuthorLevelIdsUseCase,
        GetUserInfoUseCase
{
    private final UserQueryPort userQueryPort;
    private final UserMultiQueryPort userMultiQueryPort;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;
    private final GetVisitSourceLabelFromIdUseCase getVisitSourceLabelFromIdUseCase;

    /**
     * 주어진 사용자 ID로 해당 사용자의 닉네임을 반환합니다.
     *
     * @param userId 닉네임을 조회할 사용자 ID
     * @return 사용자의 닉네임
     * @throws UserException 사용자가 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public String findUsernameById(Long userId) {
        Instant startTime = LoggerFactory.service().logStart("FindUsernameUseCase", "아이디를 통한 유저명을 찾는 서비스 시작 userId=" + userId);
        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindUsernameUseCase", "[유저명 조회] 유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
        LoggerFactory.service().logSuccess("FindUsernameUseCase", "아이디를 통한 유저명을 찾는 서비스 성공 userId=" + userId, startTime);
        return user.getNickname();
    }

    /**
     * 주어진 여러 사용자 ID에 대해 각 ID에 해당하는 닉네임을 조회하여 Map으로 반환합니다.
     *
     * @param userIds 닉네임을 조회할 사용자 ID 목록
     * @return 사용자 ID를 키, 닉네임을 값으로 하는 Map. 입력 목록이 null이거나 비어 있으면 빈 Map을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> findUsernamesByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        Instant startTime = LoggerFactory.service().logStart("FindUsernameUseCase", "주어진 사용자 ID 목록에 대해 각 ID에 해당하는 닉네임을 반환 서비스 시작");
        Map<Long, String> usernames = userMultiQueryPort.findUsernamesByIds(userIds);
        LoggerFactory.service().logSuccess("FindUsernameUseCase", "주어진 사용자 ID 목록에 대해 각 ID에 해당하는 닉네임을 반환 서비스 성공", startTime);
        return usernames;
    }

    @Override
    @Transactional(readOnly = true)
    public String findUserThumbnailById(Long userId) {
        Instant startTime = LoggerFactory.service().logStart("FindUserThumbnailUseCase", "아이디를 통한 유저 프로필 이미지 URL을 찾는 서비스 시작 userId=" + userId);
        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindUserThumbnailUseCase", "[유저 프로필 이미지 조회] 유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
        LoggerFactory.service().logSuccess("FindUserThumbnailUseCase", "아이디를 통한 유저 프로필 이미지 URL을 찾는 서비스 성공 userId=" + userId, startTime);
        return user.getProfileImageUrl();
    }

    /**
     * 여러 사용자 ID에 대해 각 사용자의 프로필 썸네일 URL을 조회하여 맵으로 반환합니다.
     *
     * @param userIds 프로필 썸네일을 조회할 사용자 ID 목록
     * @return 사용자 ID를 키, 썸네일 URL을 값으로 하는 맵. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> findUserThumbnailsByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        Instant startTime = LoggerFactory.service().logStart("FindUserThumbnailUseCase", "주어진 사용자 ID 목록에 대해 각 ID에 해당하는 프로필 이미지를 반환 서비스 시작");
        Map<Long, String> userThumbnails = userMultiQueryPort.findUserThumbnailsByIds(userIds);
        LoggerFactory.service().logSuccess("FindUserThumbnailUseCase", "주어진 사용자 ID 목록에 대해 각 ID에 해당하는 프로필 이미지를 반환 서비스 성공", startTime);
        return userThumbnails;
    }

    /**
     * 여러 사용자 ID에 대해 각 사용자의 작성자 레벨 ID를 조회하여 반환합니다.
     *
     * @param userIds 작성자 레벨 ID를 조회할 사용자 ID 목록
     * @return 사용자 ID와 해당 작성자 레벨 ID의 매핑. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> findUserAuthorLevelIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        Instant startTime = LoggerFactory.service().logStart("FindUserAuthorLevelIdsUseCase", "주어진 사용자 ID 목록에 대해 각 ID에 해당하는 작성자 유형을 반환하는 서비스 시작");
        Map<Long, String> userAuthorLevelIds = userMultiQueryPort.findUserAuthorLevelIds(userIds);
        LoggerFactory.service().logSuccess("FindUserAuthorLevelIdsUseCase", "주어진 사용자 ID 목록에 대해 각 ID에 해당하는 작성자 유형을 반환하는 서비스 성공", startTime);
        return userAuthorLevelIds;
    }

    /**
     * 주어진 사용자 ID로 사용자를 조회하여 UserInfo로 변환해 반환합니다.
     *
     * 조회 대상 사용자가 존재하지 않으면 UserException(UserErrorStatus.NOT_FOUND_USER)을 던집니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자의 상세 정보(UserInfo)
     * @throws UserException 사용자가 존재하지 않을 경우(UserErrorStatus.NOT_FOUND_USER)
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfo extractUserInfo(Long userId) {
        Instant startTime = LoggerFactory.service().logStart("GetUserInfoUseCase", "주어진 사용자 ID에 대한 유저 정보 추출 서비스 시작 userId=" + userId);

        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("GetUserInfoUseCase", "[유저 정보 추출] 유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

        UserInfo userInfo = user.toUserInfo();
        LoggerFactory.service().logSuccess("GetUserInfoUseCase", "주어진 사용자 ID에 대한 유저 정보 추출 서비스 성공 userId=" + userId, startTime);
        return userInfo;
    }

    /**
         * 주어진 사용자 ID에 대한 상세 사용자 정보를 조회하여 GetUserInfoResponse로 반환합니다.
         *
         * <p>사용자 존재 시 사용자 역할, 이메일, 닉네임, 프로필 이미지, 소개 텍스트와
         * 선택적 레이블(저자 레벨, 직업, 관심 주제 목록, 유입 경로)을 포함한 DTO를 생성해 반환합니다.</p>
         *
         * @param userId 조회할 사용자의 ID
         * @return 조회된 사용자의 정보를 담은 {@link GetUserInfoResponse}
         * @throws UserException 사용자가 존재하지 않을 경우 {@code UserErrorStatus.NOT_FOUND_USER}로 발생합니다.
         */
    @Override
    @Transactional(readOnly = true)
    public GetUserInfoResponse getUserInfo(Long userId) {
        Instant startTime = LoggerFactory.service().logStart("GetUserInfoUseCase", "주어진 사용자 ID에 대한 유저 정보 조회 서비스 시작 userId=" + userId);

        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("GetUserInfoUseCase", "[유저 정보 조회] 유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

        String authorLevelLabel = user.getAuthorLevelId() == null ? null : getAuthorLevelLabelFromIdUseCase.getLabelById(user.getAuthorLevelId());
        String occupationLabel = user.getOccupationId() == null ? null : getOccupationLabelFromIdUseCase.getLabelById(user.getOccupationId());
        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(user.getTopicIds());
        List<String> topicLabels = user.getTopicIds().stream()
                .map(topicLabelMap::get)
                .toList();
        String visitSourceLabel = user.getVisitSourceId() == null ? null : getVisitSourceLabelFromIdUseCase.getLabelById(user.getVisitSourceId());

        GetUserInfoResponse getUserInfoResponse = new GetUserInfoResponse(
                user.getId(),
                user.getRole(),
                user.getEmail(),
                user.getNickname(),
                authorLevelLabel,
                occupationLabel,
                topicLabels,
                visitSourceLabel,
                user.getProfileImageUrl(),
                user.getIntroductionText()
        );

        LoggerFactory.service().logSuccess("GetUserInfoUseCase", "주어진 사용자 ID에 대한 유저 정보 조회 서비스 성공 userId=" + userId, startTime);
        return getUserInfoResponse;
    }
}
