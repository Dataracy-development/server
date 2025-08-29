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

    private final UserQueryPort userQueryPort;

    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

    private final FindUserProjectsUseCase findUserProjectsUseCase;
    private final FindUserDataSetsUseCase findUserDataSetsUseCase;

    @Override
    @Transactional(readOnly = true)
    public GetOtherUserInfoResponse getOtherUserInfo(Long userId) {
        Instant startTime = LoggerFactory.service().logStart("GetOtherUserInfoUseCase", "주어진 사용자 ID에 대한 타인 유저 정보 조회 서비스 시작 userId=" + userId);

        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("GetOtherUserInfoUseCase", "[타인 유저 정보 조회] 유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=" + userId);
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

        LoggerFactory.service().logSuccess("GetOtherUserInfoUseCase", "주어진 사용자 ID에 대한 타인 정보 조회 서비스 성공 userId=" + userId, startTime);
        return getOtherUserInfoResponse;
    }

    @Override
    public Page<GetOtherUserProjectResponse> getOtherExtraProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("GetOtherUserInfoUseCase", "타인이 업로드한 프로젝트 목록 추가 조회 서비스 시작 userId=" + userId);
        Page<GetOtherUserProjectResponse> projects = findUserProjectsUseCase.findUserProjects(userId, pageable)
                .map(otherUserInfoMapper::toOtherUserProject);
        LoggerFactory.service().logSuccess("GetOtherUserInfoUseCase", "타인이 업로드한 프로젝트 목록 추가 조회 서비스 성공 userId=" + userId, startTime);
        return projects;
    }

    @Override
    public Page<GetOtherUserDataResponse> getOtherExtraDataSets(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("GetOtherUserInfoUseCase", "타인이 업로드한 데이터셋 목록 추가 조회 서비스 시작 userId=" + userId);
        Page<GetOtherUserDataResponse> datasets = findUserDataSetsUseCase.findUserDataSets(userId, pageable)
                .map(otherUserInfoMapper::toOtherUserData);
        LoggerFactory.service().logSuccess("GetOtherUserInfoUseCase", "타인이 업로드한 데이터셋 목록 추가 조회 서비스 성공 userId=" + userId, startTime);
        return datasets;
    }
}
