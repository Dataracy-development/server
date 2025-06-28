package com.dataracy.modules.auth.domain.model;

/**
 * 소셜 로그인으로부터 제공받는 정보 공통 인터페이스
 */
public interface OAuth2UserInfo {
    //제공자 (Ex. kakao, google, ...)
    String getProvider();
    //제공자에서 발급해주는 고유 아이디
    String getProviderId();
    //이메일
    String getEmail();
    //사용자 이름 (설정한 이름)
    String getName();
}
