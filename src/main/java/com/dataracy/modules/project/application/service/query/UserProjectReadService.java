package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.application.mapper.read.UserProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.read.FindUserProjectsUseCase;
import com.dataracy.modules.project.application.port.out.query.read.FindUserProjectsPort;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProjectReadService implements FindUserProjectsUseCase {
    private final UserProjectDtoMapper userProjectDtoMapper;

    private final FindUserProjectsPort findUserProjectsPort;

    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    @Override
    @Transactional(readOnly = true)
    public Page<UserProjectResponse> findUserProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("FindUserProjects", "해당 회원이 작성한 프로젝트 목록 조회 서비스 시작 userId=" + userId);

        Page<Project> savedProjects = findUserProjectsPort.findUserProjects(userId, pageable);

        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
        Map<Long, String> authorLevelLabelMap = getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

        Page<UserProjectResponse> findUserProjectsResponse = savedProjects.map(project -> userProjectDtoMapper.toResponseDto(
                project,
                topicLabelMap.get(project.getTopicId()),
                authorLevelLabelMap.get(project.getAuthorLevelId())
        ));

        LoggerFactory.service().logSuccess("FindUserProjects", "해당 회원이 작성한 프로젝트 목록 조회 서비스 종료 userId=" + userId, startTime);
        return findUserProjectsResponse;
    }

    @Override
    public Page<UserProjectResponse> findLikeProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("FindLikeProjects", "해당 회원이 좋아요한 프로젝트 목록 조회 서비스 시작 userId=" + userId);

        Page<Project> savedProjects = findUserProjectsPort.findLikeProjects(userId, pageable);

        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
        Map<Long, String> authorLevelLabelMap = getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

        Page<UserProjectResponse> findLikeProjectsResponse = savedProjects.map(project -> userProjectDtoMapper.toResponseDto(
                project,
                topicLabelMap.get(project.getTopicId()),
                authorLevelLabelMap.get(project.getAuthorLevelId())
        ));

        LoggerFactory.service().logSuccess("FindLikeProjects", "해당 회원이 좋아요한 프로젝트 목록 조회 서비스 종료 userId=" + userId, startTime);
        return findLikeProjectsResponse;
    }
}
