/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.impl.command;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectDataJpaRepository;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.port.out.command.create.CreateProjectPort;
import com.dataracy.modules.project.application.port.out.command.delete.DeleteProjectDataPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectFilePort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectCommandDbAdapter
    implements CreateProjectPort, UpdateProjectFilePort, UpdateProjectPort, DeleteProjectDataPort {
  private final ProjectJpaRepository projectJpaRepository;

  // Entity 및 메시지 상수 정의
  private static final String PROJECT_ENTITY = "ProjectEntity";
  private static final String PROJECT_DATA_ENTITY = "ProjectDataEntity";
  private static final String PROJECT_NOT_FOUND_MESSAGE = "해당 프로젝트가 존재하지 않습니다. projectId=";
  private static final String PARENT_PROJECT_NOT_FOUND_MESSAGE =
      "부모 프로젝트가 존재하지 않습니다. parentProjectId=";
  private final ProjectDataJpaRepository projectDataJpaRepository;

  /**
   * 프로젝트 도메인 객체를 저장하고, 저장된 최소 정보의 프로젝트 객체를 반환합니다.
   *
   * @param project 저장할 프로젝트 도메인 객체
   * @return 저장된 프로젝트의 최소 정보 도메인 객체
   * @throws ProjectException 저장에 실패한 경우 발생합니다.
   */
  @Override
  public Project saveProject(Project project) {
    try {
      ProjectEntity projectEntity =
          projectJpaRepository.save(ProjectEntityMapper.toEntity(project));
      Project savedProject = ProjectEntityMapper.toMinimal(projectEntity);
      LoggerFactory.db()
          .logSave(PROJECT_ENTITY, String.valueOf(savedProject.getId()), "프로젝트 저장이 완료되었습니다.");
      return savedProject;
    } catch (Exception e) {
      LoggerFactory.db().logError(PROJECT_ENTITY, "프로젝트 저장에 실패했습니다.", e);
      throw new ProjectException(ProjectErrorStatus.FAIL_SAVE_PROJECT);
    }
  }

  /**
   * 지정한 프로젝트의 썸네일 이미지 파일 URL을 새로운 값으로 업데이트합니다.
   *
   * <p>프로젝트 ID에 해당하는 프로젝트가 존재하지 않으면 {@code ProjectException}이 발생합니다.
   *
   * @param projectId 썸네일 URL을 변경할 프로젝트의 ID
   * @param thumbnailUrl 새로 설정할 썸네일 이미지 파일 URL
   */
  @Override
  public void updateThumbnailFile(Long projectId, String thumbnailUrl) {
    ProjectEntity projectEntity =
        projectJpaRepository
            .findById(projectId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db()
                      .logWarning(PROJECT_ENTITY, PROJECT_NOT_FOUND_MESSAGE + projectId);
                  return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });
    projectEntity.updateThumbnailUrl(thumbnailUrl);
    projectJpaRepository.save(projectEntity);
    LoggerFactory.db()
        .logUpdate(PROJECT_ENTITY, String.valueOf(projectId), "프로젝트 썸네일 이미지 파일 업데이트가 완료되었습니다.");
  }

  /**
   * 프로젝트 정보를 수정하고, 지정된 데이터 ID와의 연관 관계를 추가합니다.
   *
   * <p>주어진 프로젝트 ID로 프로젝트를 조회하여 요청 정보와 부모 프로젝트(필요한 경우)를 반영해 수정합니다. 추가할 데이터 ID 목록에 따라 프로젝트-데이터 연관
   * 엔티티를 생성하여 저장합니다. 프로젝트 또는 부모 프로젝트가 존재하지 않을 경우 `ProjectException`이 발생합니다.
   *
   * @param projectId 수정할 프로젝트의 ID
   * @param requestDto 프로젝트 수정 요청 정보
   * @param toAdd 새로 추가할 데이터 ID 집합
   * @throws ProjectException 프로젝트 또는 부모 프로젝트가 존재하지 않을 경우
   */
  @Override
  public void modifyProject(Long projectId, ModifyProjectRequest requestDto, Set<Long> toAdd) {
    ProjectEntity projectEntity =
        projectJpaRepository
            .findById(projectId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db()
                      .logWarning(PROJECT_ENTITY, PROJECT_NOT_FOUND_MESSAGE + projectId);
                  return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });

    ProjectEntity parentProject = null;
    if (requestDto.parentProjectId() != null) {
      parentProject =
          projectJpaRepository
              .findById(requestDto.parentProjectId())
              .orElseThrow(
                  () -> {
                    LoggerFactory.db()
                        .logWarning(
                            PROJECT_ENTITY,
                            PARENT_PROJECT_NOT_FOUND_MESSAGE + requestDto.parentProjectId());
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                  });
    }
    projectEntity.modify(requestDto, parentProject);

    if (!toAdd.isEmpty()) {
      List<ProjectDataEntity> newLinks =
          toAdd.stream().map(dataId -> ProjectDataEntity.of(projectEntity, dataId)).toList();
      projectDataJpaRepository.saveAll(newLinks);
    }

    projectJpaRepository.save(projectEntity);
    LoggerFactory.db().logUpdate(PROJECT_ENTITY, String.valueOf(projectId), "프로젝트 수정이 완료되었습니다.");
  }

  /**
   * 지정된 프로젝트 ID와 데이터 ID 집합에 해당하는 모든 프로젝트-데이터 연결을 삭제합니다.
   *
   * @param projectId 삭제할 프로젝트의 ID
   * @param dataIds 삭제할 데이터 ID 집합
   */
  @Override
  public void deleteByProjectIdAndDataIdIn(Long projectId, Set<Long> dataIds) {
    projectDataJpaRepository.deleteByProjectIdAndDataIdIn(projectId, dataIds);
    LoggerFactory.db()
        .logDelete(
            PROJECT_DATA_ENTITY,
            "projectId=" + projectId,
            "프로젝트-데이터 연결 " + dataIds.size() + "개 삭제");
  }
}
