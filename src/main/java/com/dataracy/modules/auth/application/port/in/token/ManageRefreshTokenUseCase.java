package com.dataracy.modules.auth.application.port.in.token;

public interface ManageRefreshTokenUseCase {
  /**
   * 지정된 사용자 ID에 대해 리프레시 토큰을 캐시에 저장합니다.
   *
   * @param userId 리프레시 토큰을 저장할 사용자 ID
   * @param refreshToken 저장할 리프레시 토큰
   */
  void saveRefreshToken(String userId, String refreshToken);

  /**
   * 지정된 사용자 ID에 해당하는 리프레시 토큰을 반환합니다.
   *
   * @param userId 리프레시 토큰을 조회할 사용자 ID
   * @return 해당 사용자 ID에 저장된 리프레시 토큰 문자열
   */
  String getRefreshToken(String userId);

  /**
   * 지정된 사용자 ID에 해당하는 리프레시 토큰을 캐시에서 삭제합니다.
   *
   * @param userId 리프레시 토큰을 삭제할 대상 사용자의 식별자
   */
  void deleteRefreshToken(String userId);
}
