package com.dataracy.modules.project.application.port.in;

public interface FindUserIdIncludingDeletedUseCase {
    Long findUserIdIncludingDeleted(Long projectId);
}
