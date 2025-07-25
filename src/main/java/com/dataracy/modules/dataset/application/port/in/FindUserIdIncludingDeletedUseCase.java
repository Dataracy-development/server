package com.dataracy.modules.dataset.application.port.in;

public interface FindUserIdIncludingDeletedUseCase {
    Long findUserIdIncludingDeleted(Long dataId);
}
