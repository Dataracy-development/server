package com.dataracy.modules.behaviorlog.application.port.out;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

public interface SaveBehaviorLogPort {
    void save(BehaviorLog log);
}
