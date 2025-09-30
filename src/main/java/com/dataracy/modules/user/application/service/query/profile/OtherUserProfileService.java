package com.dataracy.modules.user.application.service.query.profile;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.query.read.FindUserDataSetsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindUserProjectsUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import com.dataracy.modules.user.application.mapper.external.OtherUserInfoMapper;
import com.dataracy.modules.user.application.port.in.query.extractor.GetOtherUserInfoUseCase;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OtherUserProfileService implements GetOtherUserInfoUseCase {
    private final OtherUserInfoMapper otherUserInfoMapper;

    // Use Case 상수 정의
    private static final String GET_OTHER_USER_INFO_USE_CASE = "GetOtherUserInfoUseCase";
    
    // 메시지 상수 정의
    private static final String USER_NOT_FOUND_MESSAGE = "유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=";

    private final UserQueryPort userQueryPort;

    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

    private final FindUserProjectsUseCase findUserProjectsUseCase;
    private final FindUserDataSetsUseCase findUserDataSetsUseCase;

    /**
     * 주어진 사용자 ID로 타인의 공개 프로필 정보(기본 정보, 직책/등급 라벨, 대표 프로젝트·데이터)를 조회하여 반환한다.
     *
     * 반환되는 응답에는 사용자의 ID, 닉네임, 프로필 이미지, 자기소개, authorLevel 및 occupation 라벨(해당 ID가 없으면 null),
     * 그리고 대표 프로젝트 목록과 데이터셋 목록이 포함된다.
     *
     * @param userId 조회 대상 사용자의 식별자
     * @return 지정한 사용자의 공개 프로필 정보를 담은 {@link GetOtherUserInfoResponse}
     * @throws UserException 조회 대상 사용자가 존재하지 않을 경우 {@code UserErrorStatus.NOT_FOUND_USER} 상태로 발생한다.
     */
    @Override
    @Transactional(readOnly = true)
    public GetOtherUserInfoResponse getOtherUserInfo(Long userId) {
        Instant startTime = LoggerFactory.service().logStart(GET_OTHER_USER_INFO_USE_CASE, "주어진 사용자 ID에 대한 타인 유저 정보 조회 서비스 시작 userId=" + userId);

        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(GET_OTHER_USER_INFO_USE_CASE, "[타인 유저 정보 조회] " + USER_NOT_FOUND_MESSAGE + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

        String authorLevelLabel = user.getAuthorLevelId() == null ? null : getAuthorLevelLabelFromIdUseCase.getLabelById(user.getAuthorLevelId());
        String occupationLabel = user.getOccupationId() == null ? null : getOccupationLabelFromIdUseCase.getLabelById(user.getOccupationId());

        Page<GetOtherUserProjectResponse> projects = findUserProjectsUseCase.findUserProjects(userId, null)
                .map(otherUserInfoMapper::toOtherUserProject);
        Page<GetOtherUserDataResponse> datasets = findUserDataSetsUseCase.findUserDataSets(userId, null)
                .map(otherUserInfoMapper::toOtherUserData);

        GetOtherUserInfoResponse getOtherUserInfoResponse = new GetOtherUserInfoResponse(
                user.getId(),
                user.getNickname(),
                authorLevelLabel,
                occupationLabel,
                user.getProfileImageUrl(),
                user.getIntroductionText(),
                projects,
                datasets
        );

        LoggerFactory.service().logSuccess(GET_OTHER_USER_INFO_USE_CASE, "주어진 사용자 ID에 대한 타인 정보 조회 서비스 성공 userId=" + userId, startTime);
        return getOtherUserInfoResponse;
    }

    /**
     * 특정 사용자가 업로드한 프로젝트의 추가 페이지 목록을 조회한다.
     *
     * 주어진 사용자 ID에 대해 페이지네이션된 프로젝트들을 조회하여 {@code GetOtherUserProjectResponse}로 매핑한 결과를 반환한다.
     *
     * @param userId   조회할 대상 사용자의 식별자
     * @param pageable 페이지 번호·크기·정렬 정보를 포함한 페이징 파라미터
     * @return 페이지네이션된 {@code GetOtherUserProjectResponse} 객체들
     */
    @Override
    public Page<GetOtherUserProjectResponse> getOtherExtraProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart(GET_OTHER_USER_INFO_USE_CASE, "타인이 업로드한 프로젝트 목록 추가 조회 서비스 시작 userId=" + userId);
        Page<GetOtherUserProjectResponse> projects = findUserProjectsUseCase.findUserProjects(userId, pageable)
                .map(otherUserInfoMapper::toOtherUserProject);
        LoggerFactory.service().logSuccess(GET_OTHER_USER_INFO_USE_CASE, "타인이 업로드한 프로젝트 목록 추가 조회 서비스 성공 userId=" + userId, startTime);
        return projects;
    }

    /**
     * 특정 사용자가 업로드한 데이터셋의 페이징된 목록을 조회하여 매핑된 응답 페이지로 반환합니다.
     *
     * @param userId   조회 대상 사용자의 ID
     * @param pageable 페이지 및 정렬 정보
     * @return 지정한 사용자(userId)가 올린 데이터셋을 매핑한 {@code Page<GetOtherUserDataResponse>}
     */
    @Override
    public Page<GetOtherUserDataResponse> getOtherExtraDataSets(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart(GET_OTHER_USER_INFO_USE_CASE, "타인이 업로드한 데이터셋 목록 추가 조회 서비스 시작 userId=" + userId);
        Page<GetOtherUserDataResponse> datasets = findUserDataSetsUseCase.findUserDataSets(userId, pageable)
                .map(otherUserInfoMapper::toOtherUserData);
        LoggerFactory.service().logSuccess(GET_OTHER_USER_INFO_USE_CASE, "타인이 업로드한 데이터셋 목록 추가 조회 서비스 성공 userId=" + userId, startTime);
        return datasets;
    }
}
