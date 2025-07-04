package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.domain.model.reference.VisitSource;

public interface FindVisitSourceUseCase {
    VisitSource findVisitSource(Long visitSourceId);
}
