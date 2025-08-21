package com.dataracy.modules.dataset.application.port.out.command.projection;

public interface ManageDataProjectionTaskPort {
    /**
 * 지정된 데이터 투영의 삭제 상태를 갱신하는 작업을 큐에 등록한다.
 *
 * <p>데이터 식별자에 해당하는 투영 엔티티의 `deleted` 플래그를 변경하도록 비동기 작업을 생성하여
 * 처리 큐에 넣는다.</p>
 *
 * @param dataId  갱신 대상 데이터(또는 투영)의 식별자
 * @param deleted 설정할 삭제 상태(true면 삭제된 상태로 표시)
 */
void enqueueSetDeleted(Long dataId, boolean deleted);
    /**
 * 데이터 프로젝션에 대한 증분 다운로드 작업을 큐에 등록한다.
 *
 * <p>지정한 데이터 식별자에 대해 증분(delta)을 다운로드하거나 적용하는 비동기 작업을 등록한다.
 *
 * @param dataId        대상 데이터의 식별자
 * @param deltaDownload 적용하거나 다운로드할 증분 값(양수/음수 의미는 호출자 문맥에 따름)
 */
void enqueueDownloadDelta(Long dataId, int deltaDownload);
    /**
 * 지정한 ID의 데이터 ES 프로젝션 작업을 삭제합니다.
 *
 * <p>주어진 식별자에 해당하는 데이터 Elasticsearch 프로젝션 작업(큐 항목 또는 영속화된 작업 레코드)을 제거합니다.
 *
 * @param dataEsProjectionTaskId 삭제할 데이터 ES 프로젝션 작업의 식별자
 */
void delete(Long dataEsProjectionTaskId);
}
