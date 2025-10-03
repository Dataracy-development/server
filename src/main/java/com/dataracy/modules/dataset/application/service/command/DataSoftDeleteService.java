/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.service.command;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.command.content.DeleteDataUseCase;
import com.dataracy.modules.dataset.application.port.in.command.content.RestoreDataUseCase;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;

@Service
public class DataSoftDeleteService implements DeleteDataUseCase, RestoreDataUseCase {
  private final SoftDeleteDataPort softDeleteDataDbPort;
  private final ManageDataProjectionTaskPort manageDataProjectionTaskPort;

  // Use Case 상수 정의
  private static final String DELETE_DATA_USE_CASE = "DeleteDataUseCase";
  private static final String RESTORE_DATA_USE_CASE = "RestoreDataUseCase";

  /**
   * Soft delete 및 복원 유스케이스용 서비스 인스턴스를 생성합니다.
   *
   * <p>이 생성자는 데이터베이스 소프트 삭제/복원 포트와 데이터 프로젝션 업데이트 작업을 관리하는 포트를 주입받아 해당 유스케이스가 DB 변경과 외부 프로젝션(예:
   * Elasticsearch) 동기화를 수행할 수 있도록 초기화합니다.
   */
  public DataSoftDeleteService(
      @Qualifier("softDeleteDataDbAdapter") SoftDeleteDataPort softDeleteDataDbPort,
      ManageDataProjectionTaskPort manageDataProjectionTaskPort) {
    this.softDeleteDataDbPort = softDeleteDataDbPort;
    this.manageDataProjectionTaskPort = manageDataProjectionTaskPort;
  }

  /**
   * 데이터셋을 소프트 삭제 처리하고 DB와 외부 프로젝션(예: Elasticsearch)에 삭제 상태 반영을 요청합니다.
   *
   * <p>DB에서 소프트 삭제를 수행한 뒤 프로젝션 업데이트 작업을 큐에 등록합니다. 메서드는 트랜잭션으로 실행되어 예외 발생 시 데이터베이스 변경은 롤백됩니다.
   *
   * @param dataId 삭제할 데이터셋의 고유 식별자
   */
  @Override
  @Transactional
  public void deleteData(Long dataId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(DELETE_DATA_USE_CASE, "데이터셋 Soft Delete 삭제 서비스 시작 dataId=" + dataId);

    // DB만 확정
    softDeleteDataDbPort.deleteData(dataId);
    // ES 작업 큐
    manageDataProjectionTaskPort.enqueueSetDeleted(dataId, true);

    LoggerFactory.service()
        .logSuccess(DELETE_DATA_USE_CASE, "데이터셋 Soft Delete 삭제 서비스 종료 dataId=" + dataId, startTime);
  }

  /**
   * 지정한 데이터셋을 소프트 삭제 상태에서 복구하고, 복구 결과를 외부 프로젝션(예: Elasticsearch)에 반영하도록 업데이트 작업을 큐에 추가합니다.
   *
   * <p>메서드는 데이터베이스 복구와 프로젝션 업데이트 작업의 등록을 수행하며, 트랜잭션 내에서 실행되어 예외 발생 시 롤백됩니다.
   *
   * @param dataId 복구할 데이터셋의 고유 식별자
   */
  @Override
  @Transactional
  public void restoreData(Long dataId) {
    Instant startTime =
        LoggerFactory.service().logStart(RESTORE_DATA_USE_CASE, "데이터셋 복원 서비스 시작 dataId=" + dataId);

    softDeleteDataDbPort.restoreData(dataId);
    manageDataProjectionTaskPort.enqueueSetDeleted(dataId, false);

    LoggerFactory.service()
        .logSuccess(RESTORE_DATA_USE_CASE, "데이터셋 복원 서비스 종료 dataId=" + dataId, startTime);
  }
}
