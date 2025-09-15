package com.dataracy.modules.auth.adapter.web.mapper;

import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import org.springframework.stereotype.Component;

/**
 * Auth 웹 DTO와 Auth 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class AuthWebMapper {
    /**
     * 자체 로그인 웹 요청 DTO를 애플리케이션 요청 DTO로 변환합니다.
     *
     * @param webRequest 자체 로그인 웹 요청 DTO
     * @return 변환된 자체 로그인 애플리케이션 요청 DTO
     */
    public SelfLoginRequest toApplicationDto(SelfLoginWebRequest webRequest) {
        return new SelfLoginRequest(webRequest.email(), webRequest.password());
    }
}
