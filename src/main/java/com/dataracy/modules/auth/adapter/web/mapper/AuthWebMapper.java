package com.dataracy.modules.auth.adapter.web.mapper;

import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import org.springframework.stereotype.Component;

/**
 * Auth 웹 DTO와 Auth 도메인 DTO를 변환하는 매퍼
 */
@Component
public class AuthWebMapper {
    /**
     *
     * @param webRequest
     * @return
     */
    public SelfLoginRequest toApplicationDto(SelfLoginWebRequest webRequest) {
        return new SelfLoginRequest(webRequest.email(), webRequest.password());
    }
}
