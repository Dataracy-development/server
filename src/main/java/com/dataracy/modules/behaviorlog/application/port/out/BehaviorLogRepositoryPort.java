package com.dataracy.modules.behaviorlog.application.port.out;

/**
 * 로그인 시 익명 사용자로 기록된 로그들을 userId로 병합
 */
public interface BehaviorLogRepositoryPort {
    void updateUserIdByAnonymousId(String userId, String anonymousId);
}
