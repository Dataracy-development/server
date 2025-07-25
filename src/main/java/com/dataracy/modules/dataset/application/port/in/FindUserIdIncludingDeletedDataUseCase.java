package com.dataracy.modules.dataset.application.port.in;

public interface FindUserIdIncludingDeletedDataUseCase {
    Long findUserIdIncludingDeleted(Long dataId);
}
