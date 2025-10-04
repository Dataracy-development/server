package com.dataracy.modules.auth.application.port.in.token;

public interface BlackListTokenUseCase {
  /**
   * 전달된 토큰을 블랙리스트에 등록한다.
   *
   * <p>블랙리스트 항목은 지정한 만료 기간(밀리초) 이후 자동으로 무효화되어 더 이상 블랙리스트로 간주되지 않는다.
   *
   * @param token 블랙리스트에 추가할 JWT 또는 토큰 문자열
   * @param expirationMillis 블랙리스트 유효 기간(밀리초 단위). 현재 시점부터 이 값 만큼 지난 후 항목이 만료된다.
   */
  void addToBlackList(String token, long expirationMillis);

  /**
   * 주어진 토큰이 블랙리스트에 등록되어 있는지 검사한다.
   *
   * <p>주로 로그아웃 처리나 토큰 무효화 시 사용되는 토큰이 현재 블랙리스트에 포함되어 있으면 true를 반환한다.
   *
   * @param token 검사할 토큰 문자열 (예: JWT)
   * @return 블랙리스트에 있으면 {@code true}, 그렇지 않으면 {@code false}
   */
  boolean isBlacklisted(String token);
}
