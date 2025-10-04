package com.dataracy.modules.project.application.service.query;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.application.mapper.read.UserProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.read.FindUserProjectsUseCase;
import com.dataracy.modules.project.application.port.out.query.read.FindUserProjectsPort;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProjectReadService implements FindUserProjectsUseCase {
  private final UserProjectDtoMapper userProjectDtoMapper;

  private final FindUserProjectsPort findUserProjectsPort;

  private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
  private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

  // Use Case 상수 정의
  private static final String FIND_USER_PROJECTS_USE_CASE = "FindUserProjectsUseCase";
  private static final String FIND_LIKE_PROJECTS_USE_CASE = "FindLikeProjectsUseCase";

  /**
   * 지정된 사용자가 작성한 프로젝트들을 페이지 단위로 조회하여 각 프로젝트의 주제 및 저자 레벨 라벨을 포함한 응답 DTO 페이지를 반환합니다.
   *
   * <p>데이터는 저장소에서 페이징으로 조회한 Project 엔티티를 기반으로 하며, 조회된 프로젝트들의 topicId와 authorLevelId를 모아서 각각의 라벨을
   * 조회한 뒤 UserProjectResponse로 매핑합니다.
   *
   * @param userId 조회 대상 사용자 식별자
   * @param pageable 페이지 및 정렬 정보를 담은 객체
   * @return 각 프로젝트에 topic/author level 라벨이 포함된 UserProjectResponse의 Page (조회 결과가 없으면 빈 Page)
   */
  @Override
  @Transactional(readOnly = true)
  public Page<UserProjectResponse> findUserProjects(Long userId, Pageable pageable) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(FIND_USER_PROJECTS_USE_CASE, "해당 회원이 작성한 프로젝트 목록 조회 서비스 시작 userId=" + userId);

    Page<Project> savedProjects = findUserProjectsPort.findUserProjects(userId, pageable);

    List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
    List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

    Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
    Map<Long, String> authorLevelLabelMap =
        getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

    Page<UserProjectResponse> findUserProjectsResponse =
        savedProjects.map(
            project ->
                userProjectDtoMapper.toResponseDto(
                    project,
                    topicLabelMap.get(project.getTopicId()),
                    authorLevelLabelMap.get(project.getAuthorLevelId())));

    LoggerFactory.service()
        .logSuccess(
            FIND_USER_PROJECTS_USE_CASE,
            "해당 회원이 작성한 프로젝트 목록 조회 서비스 종료 userId=" + userId,
            startTime);
    return findUserProjectsResponse;
  }

  /**
   * 특정 사용자가 '좋아요'한 프로젝트들의 페이지를 조회하여 응답 DTO로 변환하여 반환합니다.
   *
   * <p>각 프로젝트의 topicId와 authorLevelId에 대해 레이블을 조회해 DTO에 포함합니다.
   *
   * @param userId 조회 대상 사용자 ID
   * @param pageable 페이지네이션 정보
   * @return 페이지로 포장된 UserProjectResponse 목록 (프로젝트별로 topic/author level 레이블이 포함됨)
   */
  @Override
  public Page<UserProjectResponse> findLikeProjects(Long userId, Pageable pageable) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_LIKE_PROJECTS_USE_CASE, "해당 회원이 좋아요한 프로젝트 목록 조회 서비스 시작 userId=" + userId);

    Page<Project> savedProjects = findUserProjectsPort.findLikeProjects(userId, pageable);

    List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
    List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

    Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
    Map<Long, String> authorLevelLabelMap =
        getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

    Page<UserProjectResponse> findLikeProjectsResponse =
        savedProjects.map(
            project ->
                userProjectDtoMapper.toResponseDto(
                    project,
                    topicLabelMap.get(project.getTopicId()),
                    authorLevelLabelMap.get(project.getAuthorLevelId())));

    LoggerFactory.service()
        .logSuccess(
            FIND_LIKE_PROJECTS_USE_CASE,
            "해당 회원이 좋아요한 프로젝트 목록 조회 서비스 종료 userId=" + userId,
            startTime);
    return findLikeProjectsResponse;
  }
}
