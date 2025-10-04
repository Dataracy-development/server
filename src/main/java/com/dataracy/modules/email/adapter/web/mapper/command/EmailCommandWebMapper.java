package com.dataracy.modules.email.adapter.web.mapper.command;

import org.springframework.stereotype.Component;

import com.dataracy.modules.email.adapter.web.request.command.SendEmailWebRequest;
import com.dataracy.modules.email.application.dto.request.command.SendEmailRequest;

/** 이메일 Command 웹 DTO와 이메일 애플리케이션 DTO를 서로 변환하기 위한 매퍼 */
@Component
public class EmailCommandWebMapper {
  /**
   * 이메일 인증 웹 요청 DTO를 애플리케이션 계층의 이메일 인증 요청 DTO로 변환합니다.
   *
   * @param webRequest 변환할 이메일 인증 웹 요청 DTO
   * @return 변환된 이메일 인증 애플리케이션 요청 DTO
   */
  public SendEmailRequest toApplicationDto(SendEmailWebRequest webRequest) {
    return new SendEmailRequest(webRequest.email(), webRequest.purpose());
  }
}
