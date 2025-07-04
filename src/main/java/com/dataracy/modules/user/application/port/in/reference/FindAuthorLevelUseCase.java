package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.domain.model.reference.AuthorLevel;

public interface FindAuthorLevelUseCase {
    AuthorLevel findAuthorLevel(Long authorLevelId);
}
