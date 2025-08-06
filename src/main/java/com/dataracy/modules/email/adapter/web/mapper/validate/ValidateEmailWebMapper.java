package com.dataracy.modules.email.adapter.web.mapper.validate;

import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
import com.dataracy.modules.email.application.dto.request.validate.VerifyCodeRequest;
import org.springframework.stereotype.Component;

/**
 * 이메일 웹 DTO와 이메일 도메인 DTO를 서로 변환하기 위한 매퍼
 */
@Component
public class ValidateEmailWebMapper {
    // 이메일 인증 코드 검증 웹 요청 DTO -> 이메일 인증 코드 검증 도메인 요청 DTO
    public VerifyCodeRequest toApplicationDto(VerifyCodeWebRequest webRequest) {
        return new VerifyCodeRequest(webRequest.email(), webRequest.code(), webRequest.purpose());
    }
}
