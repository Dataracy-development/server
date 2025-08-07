package com.dataracy.modules.auth.application.port.in.cache;

public interface CacheResetTokenUseCase {
    /**
 * 주어진 리셋 토큰을 캐시에 저장합니다.
 *
 * @param token 저장할 리셋 토큰 문자열
 */
void saveResetToken(String token);
    /**
 * 주어진 리셋 토큰이 유효한지 확인합니다.
 *
 * @param token 검증할 리셋 토큰 문자열
 * @return 토큰이 유효하면 true, 그렇지 않으면 false
 */
boolean isValidResetToken(String token);
}
