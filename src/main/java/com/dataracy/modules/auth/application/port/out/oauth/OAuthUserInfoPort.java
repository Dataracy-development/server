package com.dataracy.modules.auth.application.port.out.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

import java.util.Map;

public interface OAuthUserInfoPort {
    /**
     * 소셜 서버로부터 받은 유저 정보를 토대로 추출하여 DTO에 저장한다.
     *
     * @param provider 소셜 제공자
     * @param attributes 소셜 제공자로부터 받은 정보
     * @return 추출하여 변환한 유저 정보 DTO
     */
    OAuthUserInfo extract(String provider, Map<String, Object> attributes);
}
