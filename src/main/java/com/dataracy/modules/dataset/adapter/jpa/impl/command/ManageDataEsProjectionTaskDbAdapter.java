package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import org.springframework.stereotype.Component;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ManageDataEsProjectionTaskDbAdapter implements ManageDataProjectionTaskPort {
  private final DataEsProjectionTaskRepository repo;

  /**
   * 데이터의 삭제 상태 변경 작업을 큐에 등록하여 저장소에 영속화한다.
   *
   * @param dataId 변경할 데이터의 식별자
   * @param deleted 설정할 삭제 상태 (true면 삭제로 표시)
   */
  @Override
  public void enqueueSetDeleted(Long dataId, boolean deleted) {
    repo.save(DataEsProjectionTaskEntity.builder().dataId(dataId).setDeleted(deleted).build());
  }

  /**
   * 특정 데이터에 대해 다운로드 수 변경을 반영하는 작업을 큐(저장소)에 등록한다.
   *
   * <p>데이터 식별자에 해당하는 작업 엔티티를 생성하여 저장소에 persist한다. `deltaDownload`는 적용할 다운로드 수 증감량이며, 양수는 증가, 음수는
   * 감소로 해석된다.
   *
   * @param dataId 변경 대상 데이터의 식별자
   * @param deltaDownload 적용할 다운로드 수의 증감량(양수: 증가, 음수: 감소)
   */
  @Override
  public void enqueueDownloadDelta(Long dataId, int deltaDownload) {
    repo.save(
        DataEsProjectionTaskEntity.builder().dataId(dataId).deltaDownload(deltaDownload).build());
  }

  /**
   * 지정한 ID의 데이터 ES 프로젝션 작업 엔티티를 삭제한다.
   *
   * @param dataEsProjectionTaskId 삭제할 DataEsProjectionTask의 식별자
   */
  @Override
  public void delete(Long dataEsProjectionTaskId) {
    repo.deleteById(dataEsProjectionTaskId);
  }
}
