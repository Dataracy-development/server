package com.dataracy.modules.project.application.port.out;

import java.util.Set;

public interface ProjectViewCountRedisPort {
    /**
 * 지정된 프로젝트와 대상 유형에 대해 뷰어의 조회수를 증가시킵니다.
 *
 * @param projectId 조회수를 증가시킬 프로젝트의 ID
 * @param viewerId  조회를 수행한 뷰어의 식별자
 * @param targetType 조회수 대상의 유형
 */
void increaseViewCount(Long projectId, String viewerId, String targetType);
    /**
 * 지정된 프로젝트와 대상 유형에 대한 현재 조회수를 반환합니다.
 *
 * @param projectId 조회수를 조회할 프로젝트의 ID
 * @param targetType 조회 대상 유형
 * @return 해당 프로젝트와 대상 유형의 현재 조회수
 */
Long getViewCount(Long projectId, String targetType);
    /**
 * 지정된 타겟 타입에 대한 모든 조회수 관련 키의 집합을 반환합니다.
 *
 * @param targetType 조회수 키를 조회할 타겟 타입
 * @return 해당 타겟 타입의 모든 조회수 키 집합
 */
Set<String> getAllViewCountKeys(String targetType);
    /**
 * 지정된 대상 ID와 대상 유형에 대한 조회수를 초기화합니다.
 *
 * @param targetId   조회수를 초기화할 대상의 ID
 * @param targetType 조회수를 초기화할 대상의 유형
 */
void clearViewCount(Long targetId, String targetType);
}
