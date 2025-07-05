package com.dataracy.modules.auth.application.port.out.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

import java.util.Map;

/**
 * 소셜 서버로부터 유저 정보 추출 및 저장 포트
 */
public interface OAuthUserInfoPort {
    OAuthUserInfo extract(String provider, Map<String, Object> attributes);
}
