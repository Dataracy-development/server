package com.dataracy.modules.project.application.port.out.command.delete;

import java.util.Set;

public interface DeleteProjectDataPort {
    /****
 * 지정된 프로젝트 ID와 데이터 ID 집합에 해당하는 데이터를 삭제합니다.
 *
 * @param projectId 삭제할 데이터가 속한 프로젝트의 ID
 * @param dataIds 삭제할 데이터의 ID 집합
 */
void deleteByProjectIdAndDataIdIn(Long projectId, Set<Long> dataIds);
}
