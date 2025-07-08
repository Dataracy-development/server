package com.dataracy.modules.logging.application.port.out;

import com.dataracy.modules.logging.domain.model.BehaviorLog;

public interface BehaviorLogRepositoryPort {
    void save(BehaviorLog log);
}
