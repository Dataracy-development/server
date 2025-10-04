package com.dataracy.modules.project.application.port.out.command.projection;

import java.util.Map;

public interface ManageProjectProjectionTaskPort {
  /**
   * 프로젝트의 댓글 수 변경치를 프로젝션 업데이트 작업으로 큐에 등록한다.
   *
   * <p>등록되는 델타는 양수(증가) 또는 음수(감소)를 허용하며, 실제 프로젝션 반영은 비동기적으로 처리된다.
   *
   * @param projectId 변경할 대상 프로젝트의 식별자
   * @param deltaComment 댓글 수의 변경치(증가: 양수, 감소: 음수)
   */
  void enqueueCommentDelta(Long projectId, int deltaComment);

  /**
   * 프로젝트의 좋아요 수 변경량을 프로젝션 작업 큐에 등록한다.
   *
   * <p>deltaLike 값만큼 해당 프로젝트의 좋아요 합계에 증감하는 비동기 프로젝션 업데이트를 요청한다.
   *
   * @param projectId 대상 프로젝트의 식별자
   * @param deltaLike 증가(양수) 또는 감소(음수)될 좋아요 수의 정수 값
   */
  void enqueueLikeDelta(Long projectId, int deltaLike);

  /**
   * 프로젝트의 조회수 변경(delta)을 비동기 처리용 큐에 등록한다.
   *
   * <p>프로젝트 식별자에 해당하는 엔티티의 조회수에 적용할 증감량(deltaView)을 큐에 기록하여 이후 비동기 프로젝션 처리에서 반영되도록 한다.
   *
   * @param projectId 조회수 변경을 적용할 프로젝트의 식별자
   * @param deltaView 적용할 조회수 증감량(daily/increment 같은 단위가 아니라 전체 적용할 정수값)
   */
  void enqueueViewDelta(Long projectId, Long deltaView);

  /**
   * 프로젝트의 projection에 대해 삭제 플래그를 큐에 등록한다.
   *
   * <p>이 작업은 비동기적으로 처리될 projection 업데이트(삭제 상태 설정)를 예약한다.
   *
   * @param projectId 대상 프로젝트의 식별자
   * @param deleted 설정할 삭제 상태(true면 삭제로 표시)
   */
  void enqueueSetDeleted(Long projectId, boolean deleted);

  /**
   * 지정한 프로젝트 ES 프로젝션 작업을 삭제한다.
   *
   * @param projectEsProjectionTaskId 삭제할 프로젝션 작업의 고유 ID
   */
  void delete(Long projectEsProjectionTaskId);

  /**
   * 여러 프로젝트의 조회수 변경을 배치로 프로젝션 큐에 등록합니다.
   *
   * @param viewCountUpdates 프로젝트 ID와 조회수 변경량의 맵
   */
  void enqueueViewDeltaBatch(Map<Long, Long> viewCountUpdates);
}
