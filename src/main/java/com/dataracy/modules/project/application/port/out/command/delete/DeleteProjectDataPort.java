package com.dataracy.modules.project.application.port.out.command.delete;

import java.util.Set;

public interface DeleteProjectDataPort {
    void deleteByProjectIdAndDataIdIn(Long projectId, Set<Long> dataIds);
}
