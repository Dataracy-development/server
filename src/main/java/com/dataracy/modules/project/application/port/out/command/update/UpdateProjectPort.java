package com.dataracy.modules.project.application.port.out.command.update;

import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;

import java.util.Set;

public interface UpdateProjectPort {
    /**
     * 지정된 프로젝트 ID에 해당하는 프로젝트를 주어진 수정 요청 데이터로 변경합니다.
     *
     * @param projectId 수정할 프로젝트의 ID
     * @param requestDto 프로젝트 수정에 필요한 데이터가 담긴 요청 객체
     */
    void modifyProject(Long projectId, ModifyProjectRequest requestDto, Set<Long> toAdd);
}
