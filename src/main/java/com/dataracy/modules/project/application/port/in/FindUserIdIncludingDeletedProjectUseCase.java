package com.dataracy.modules.project.application.port.in;

public interface FindUserIdIncludingDeletedProjectUseCase {
    Long findUserIdIncludingDeleted(Long projectId);
}
