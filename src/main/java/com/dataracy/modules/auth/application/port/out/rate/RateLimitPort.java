package com.dataracy.modules.auth.application.port.out.rate;

/** 레이트 리미팅을 위한 포트 인터페이스 IP 기반으로 요청 횟수를 제한합니다. */
public interface RateLimitPort {
  /**
   * 지정된 키(IP)에 대해 요청이 허용되는지 확인합니다.
   *
   * @param key 레이트 리미팅을 적용할 키 (예: IP 주소)
   * @param maxRequests 최대 허용 요청 수
   * @param windowMinutes 시간 윈도우 (분)
   * @return 요청이 허용되면 true, 그렇지 않으면 false
   */
  boolean isAllowed(String key, int maxRequests, int windowMinutes);

  /**
   * 지정된 키(IP)의 요청 카운트를 증가시킵니다.
   *
   * @param key 레이트 리미팅을 적용할 키 (예: IP 주소)
   * @param windowMinutes 시간 윈도우 (분)
   */
  void incrementRequestCount(String key, int windowMinutes);
}
