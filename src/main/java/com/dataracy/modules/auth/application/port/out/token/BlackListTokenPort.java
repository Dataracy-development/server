package com.dataracy.modules.auth.application.port.out.token;

public interface BlackListTokenPort {
    /**
 * 지정한 토큰을 블랙리스트에 등록한다.
 *
 * 등록된 토큰은 재사용을 차단하기 위해 블랙리스트에 저장된다.
 *
 * @param token 블랙리스트에 추가할 JWT 등 토큰 문자열
 * @param expirationMillis 현재 시점으로부터의 만료 기간(밀리초). 이 시간이 지나면 블랙리스트 항목은 만료되어 더 이상 블랙리스트로 취급되지 않아야 한다.
 */
void setBlackListToken(String token, long expirationMillis);
    /**
 * 주어진 토큰이 현재 블랙리스트에 등록되어 있는지 확인합니다.
 *
 * @param token 검사할 토큰 문자열(JWT 등)
 * @return 블랙리스트에 등록되어 있으면 {@code true}, 그렇지 않으면 {@code false}
 */
boolean isBlacklisted(String token);
}
