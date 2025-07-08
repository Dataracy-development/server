package com.dataracy.modules.behaviorlog.application.port.out;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

public interface BehaviorLogRepositoryPort {
    void save(BehaviorLog log);
}
