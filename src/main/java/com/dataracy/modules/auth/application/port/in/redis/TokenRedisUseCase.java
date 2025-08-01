package com.dataracy.modules.auth.application.port.in.redis;

public interface TokenRedisUseCase {
    /**
 * 지정된 유저 ID에 해당하는 리프레시 토큰을 Redis에 저장합니다.
 *
 * @param userId 리프레시 토큰을 저장할 대상 유저의 ID
 * @param refreshToken 저장할 리프레시 토큰 값
 */
    void saveRefreshToken(String userId, String refreshToken);

    /**
 * 주어진 유저 아이디에 해당하는 리프레시 토큰을 레디스에서 조회하여 반환합니다.
 *
 * @param userId 리프레시 토큰을 조회할 유저의 아이디
 * @return 해당 유저의 리프레시 토큰 문자열
 */
    String getRefreshToken(String userId);

    /**
 * 지정된 유저 아이디에 해당하는 리프레시 토큰을 레디스에서 삭제합니다.
 *
 * @param userId 리프레시 토큰을 삭제할 대상 유저의 아이디
 */
    void deleteRefreshToken(String userId);
}
