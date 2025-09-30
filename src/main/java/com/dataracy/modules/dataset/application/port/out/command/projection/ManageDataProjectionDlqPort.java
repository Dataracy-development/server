package com.dataracy.modules.dataset.application.port.out.command.projection;

public interface ManageDataProjectionDlqPort {
    /**
 * 데이터 프로젝션 처리 실패 항목을 DLQ(Dead Letter Queue)로 저장한다.
 *
 * 프로젝션 갱신 중 오류가 발생한 데이터의 식별자, 다운로드 증분(delta),
 * 삭제 플래그, 및 마지막 오류 메시지를 기록하기 위해 사용된다.
 *
 * @param dataId       오류가 발생한 대상 데이터의 식별자
 * @param deltaDownload 적용되려던 다운로드 증분 값 (없을 수 있음)
 * @param setDeleted   데이터가 삭제되어야 함을 표시하는 플래그 (없을 수 있음)
 * @param lastError    발생한 마지막 오류 메시지 또는 예외 설명 (없을 수 있음)
 */
void save(Long dataId, Integer deltaDownload, Boolean setDeleted, String lastError);
}
