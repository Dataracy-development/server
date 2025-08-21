package com.dataracy.modules.project.application.port.out.command.projection;

public interface ManageProjectProjectionDlqPort {
    /**
 * 프로젝트 프로젝션 관련 DLQ(Dead Letter Queue) 항목을 저장한다.
 *
 * <p>프로젝트 식별자에 대해 발생한 증분(댓글, 좋아요, 조회수)과 삭제 플래그, 마지막 에러 메시지를
 * 영속화하여 DLQ 상태를 기록한다. 주로 비동기 처리 실패나 재시도 대상 데이터를 보존할 때 사용된다.</p>
 *
 * @param projectId   대상 프로젝트의 ID
 * @param deltaComment 댓글 수의 증분(증가 또는 감소). null인 경우 변경 없음으로 처리될 수 있다.
 * @param deltaLike    좋아요 수의 증분(증가 또는 감소). null일 수 있다.
 * @param deltaView    조회수의 증분(증가 또는 감소). null일 수 있다.
 * @param setDeleted   해당 프로젝션을 삭제 상태로 표시할지 여부. null이면 상태 변경 없음으로 간주될 수 있다.
 * @param lastError    마지막으로 발생한 오류 메시지(가능한 원인 또는 스택 요약). null 허용
 */
void save(Long projectId, Integer deltaComment, Integer deltaLike, Long deltaView, Boolean setDeleted, String lastError);
}
