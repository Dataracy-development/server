package com.dataracy.modules.auth.adapter.web.mapper;

import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import org.springframework.stereotype.Component;

/**
 * Auth 웹 DTO와 Auth 도메인 DTO를 변환하는 매퍼
 */
@Component
public class AuthWebMapper {
    // 자체 로그인 웹 요청 DTO -> 자체 로그인 도메인 요청 DTO
    public SelfLoginRequest toApplicationDto(SelfLoginWebRequest webRequest) {
        return new SelfLoginRequest(webRequest.email(), webRequest.password());
    }

    // 전체 토픽 리스트 조회 도메인 응답 DTO -> 전체 토픽 리스트 조회 웹 응답 DTO

}
