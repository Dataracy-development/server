package com.dataracy.modules.auth.application.port.in.redis;

public interface TokenRedisUseCase {
    /**
     * 리프레시 토큰 레디스에 저장
     *
     * @param userId 유저 아이디
     * @param refreshToken 리프레시 토큰
     */
    void saveRefreshToken(String userId, String refreshToken);

    /**
     * 레디스에서 유저 아이디에 해당하는 리프레시 토큰을 반환
     *
     * @param userId 유저 아이디
     * @return 리프레시 토큰
     */
    String getRefreshToken(String userId);

    /**
     * 레디스에서 유저 아이디에 해당하는 리프레시 토큰을 삭제
     *
     * @param userId 유저 아이디
     */
    void deleteRefreshToken(String userId);
}
