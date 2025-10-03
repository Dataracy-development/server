/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.impl.command;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ManageProjectEsProjectionTaskDbAdapter implements ManageProjectProjectionTaskPort {
  private final ProjectEsProjectionTaskRepository repo;

  /**
   * 지정한 프로젝트에 대한 댓글 수 증감(delta)을 데이터베이스에 큐잇(저장)합니다.
   *
   * <p>이 메서드는 댓글 수 변경을 나타내는 projection task 엔티티를 생성해 저장소에 저장함으로써 이후 프로젝션 적용 시 해당 프로젝트의 댓글 수를 조정할 수
   * 있도록 합니다.
   *
   * @param projectId 변경 대상 프로젝트의 식별자
   * @param deltaComment 적용할 댓글 수 증감값(음수는 감소, 양수는 증가)
   */
  @Override
  public void enqueueCommentDelta(Long projectId, int deltaComment) {
    repo.save(
        ProjectEsProjectionTaskEntity.builder()
            .projectId(projectId)
            .deltaComment(deltaComment)
            .build());
  }

  /**
   * 주어진 프로젝트에 대한 '좋아요' 증감(deltaLike)을 비동기 처리할 projection task를 저장한다.
   *
   * <p>deltaLike는 증가(양수) 또는 감소(음수)를 나타내는 정수 값이다.
   *
   * @param projectId 수정할 대상 프로젝트의 ID
   * @param deltaLike 적용할 좋아요 수의 증감량(양수 또는 음수)
   */
  @Override
  public void enqueueLikeDelta(Long projectId, int deltaLike) {
    repo.save(
        ProjectEsProjectionTaskEntity.builder().projectId(projectId).deltaLike(deltaLike).build());
  }

  /**
   * 주어진 프로젝트에 대한 조회수(deltaView) 변경을 나타내는 프로젝션 작업을 생성하여 저장한다.
   *
   * <p>영속 저장소에 프로젝션 작업 엔티티를 저장하여 조회수 변경 처리가 나중에 적용되도록 대기열에 추가한다.
   *
   * @param projectId 조회수 변경을 적용할 프로젝트의 ID
   * @param deltaView 적용할 조회수 증감 값(증가/감소를 나타내는 정수)
   */
  @Override
  public void enqueueViewDelta(Long projectId, Long deltaView) {
    repo.save(
        ProjectEsProjectionTaskEntity.builder().projectId(projectId).deltaView(deltaView).build());
  }

  /**
   * 주어진 프로젝트에 대해 projection 작업으로 삭제 상태를 큐에 저장한다.
   *
   * <p>영속 저장소에 "삭제 상태 변경" 작업 엔티티를 생성해 저장하므로, 이후 프로젝션 처리 파이프라인에서 해당 프로젝트의 삭제 플래그가 반영된다.
   *
   * @param projectId 변경할 대상 프로젝트의 식별자
   * @param deleted 설정할 삭제 상태 (true면 삭제로 표시)
   */
  @Override
  public void enqueueSetDeleted(Long projectId, boolean deleted) {
    repo.save(
        ProjectEsProjectionTaskEntity.builder().projectId(projectId).setDeleted(deleted).build());
  }

  /**
   * 지정한 프로젝트 프로젝션 작업 엔티티를 즉시 삭제합니다.
   *
   * @param projectEsProjectionTaskId 삭제할 프로젝션 작업의 식별자(ID)
   */
  @Override
  public void delete(Long projectEsProjectionTaskId) {
    repo.deleteImmediate(projectEsProjectionTaskId);
  }

  /**
   * 여러 프로젝트의 조회수 변경을 배치로 프로젝션 큐에 등록합니다.
   *
   * @param viewCountUpdates 프로젝트 ID와 조회수 변경량의 맵
   */
  @Override
  public void enqueueViewDeltaBatch(Map<Long, Long> viewCountUpdates) {
    if (viewCountUpdates.isEmpty()) {
      return;
    }

    List<ProjectEsProjectionTaskEntity> tasks =
        viewCountUpdates.entrySet().stream()
            .map(
                entry ->
                    ProjectEsProjectionTaskEntity.builder()
                        .projectId(entry.getKey())
                        .deltaView(entry.getValue())
                        .build())
            .toList();

    repo.saveAll(tasks);
  }
}
