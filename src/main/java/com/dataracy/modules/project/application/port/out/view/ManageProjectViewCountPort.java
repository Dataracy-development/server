package com.dataracy.modules.project.application.port.out.view;

import java.util.Set;

public interface ManageProjectViewCountPort {
    /**
     * 특정 프로젝트와 대상 유형에 대해 주어진 뷰어의 조회수를 1 증가시킵니다.
     *
     * @param projectId 조회수를 증가시킬 프로젝트의 ID
     * @param viewerId 조회를 수행한 뷰어의 식별자
     * @param targetType 조회수 대상의 유형
     */
    void increaseViewCount(Long projectId, String viewerId, String targetType);

    /**
     * 지정된 프로젝트와 대상 유형에 대한 현재 조회수를 반환합니다.
     *
     * @param projectId 조회수를 확인할 프로젝트의 ID
     * @param targetType 조회수를 확인할 대상 유형
     * @return 해당 프로젝트와 대상 유형의 현재 조회수
     */
    Long getViewCount(Long projectId, String targetType);

    /**
     * 지정된 타겟 타입에 해당하는 모든 조회수 키의 집합을 반환합니다.
     *
     * @param targetType 조회수 키를 조회할 대상의 타입
     * @return 해당 타겟 타입과 관련된 모든 조회수 키의 집합
     */
    Set<String> getAllViewCountKeys(String targetType);

    /**
 * 지정된 대상(targetId)과 대상 유형(targetType)에 대한 조회수를 초기화(영으로 리셋)합니다.
 *
 * @param targetId   조회수를 초기화할 대상의 ID
 * @param targetType 조회수를 구분하는 대상 유형 문자열(예: "project", "post" 등)
 */
    void clearViewCount(Long targetId, String targetType);

    /**
 * 지정된 프로젝트와 대상 유형에 대한 현재 조회수를 반환하고 초기화(0으로 재설정)합니다.
 *
 * 호출 시 해당 프로젝트·대상 유형의 누적 조회수를 읽어 반환한 뒤, 조회수를 초기 상태로 재설정합니다.
 *
 * @param projectId 조회수를 확인하고 초기화할 프로젝트의 ID
 * @param targetType 조회수를 확인하고 초기화할 대상 유형
 * @return 초기화 전에 기록되어 있던 조회수
 */
    Long popViewCount(Long projectId, String targetType);
}
