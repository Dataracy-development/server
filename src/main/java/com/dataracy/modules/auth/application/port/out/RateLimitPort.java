package com.dataracy.modules.auth.application.port.out;

/**
 * Rate Limiting을 위한 Port 인터페이스
 * Clean Architecture의 Port Layer에 해당
 */
public interface RateLimitPort {
    /**
     * 요청이 허용되는지 확인
     * @param key 클라이언트 식별자 (IP, User ID 등)
     * @param maxRequests 최대 허용 요청 수
     * @param windowMinutes 시간 윈도우 (분)
     * @return 허용 여부
     */
    boolean isAllowed(String key, int maxRequests, int windowMinutes);
    
    /**
     * 요청 카운트 증가
     * @param key 클라이언트 식별자
     * @param incrementBy 증가량 (기본 1)
     */
    void incrementRequestCount(String key, int incrementBy);
}
