package com.dataracy.modules.auth.application.port.out.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

/**
 * 토큰 유효성 검사 및 정보 추출 포트
 */
public interface JwtValidatorPort {
    /**
 * 주어진 JWT 토큰의 유효성을 검사합니다.
 *
 * @param token 검증할 JWT 토큰 문자열
 */
void validateToken (String token);

    /**
 * JWT 토큰에서 사용자 ID를 추출하여 반환합니다.
 *
 * @param token 사용자 정보를 포함한 JWT 토큰
 * @return 토큰에 포함된 사용자 ID
 */
Long getUserIdFromToken(String token);
    /**
 * JWT 토큰에서 사용자의 역할(RoleType)을 추출합니다.
 *
 * @param token 역할 정보를 포함한 JWT 토큰
 * @return 토큰에 포함된 사용자의 역할
 */
RoleType getRoleFromToken(String token);

    /**
 * 등록 토큰에서 제공자(provider) 이름을 추출하여 반환합니다.
 *
 * @param token 등록 토큰 문자열
 * @return 토큰에 포함된 제공자 이름
 */
String getProviderFromRegisterToken(String token);
    /**
 * 등록 토큰에서 공급자 ID를 추출하여 반환합니다.
 *
 * @param token 공급자 ID를 포함하는 등록용 JWT 토큰
 * @return 추출된 공급자 ID 문자열
 */
String getProviderIdFromRegisterToken(String token);
    /**
 * 등록 토큰에서 이메일 주소를 추출하여 반환합니다.
 *
 * @param token 이메일 정보를 포함한 등록 토큰
 * @return 추출된 이메일 주소
 */
String getEmailFromRegisterToken(String token);
}
