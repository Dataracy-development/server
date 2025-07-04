package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.domain.model.reference.Occupation;

public interface FindOccupationUseCase {
    Occupation findOccupation(Long occupationId);
}
