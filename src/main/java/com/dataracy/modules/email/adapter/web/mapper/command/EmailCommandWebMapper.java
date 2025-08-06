package com.dataracy.modules.email.adapter.web.mapper.command;

import com.dataracy.modules.email.adapter.web.request.command.SendEmailWebRequest;
import com.dataracy.modules.email.application.dto.request.command.SendEmailRequest;
import org.springframework.stereotype.Component;

/**
 * 이메일 웹 DTO와 이메일 도메인 DTO를 서로 변환하기 위한 매퍼
 */
@Component
public class EmailCommandWebMapper {
    // 이메일 인증 웹 요청 DTO -> 이메일 인증 도메인 요청 DTO
    public SendEmailRequest toApplicationDto(SendEmailWebRequest webRequest) {
        return new SendEmailRequest(webRequest.email(), webRequest.purpose());
    }
}
