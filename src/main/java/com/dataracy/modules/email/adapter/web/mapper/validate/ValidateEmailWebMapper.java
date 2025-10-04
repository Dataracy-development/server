package com.dataracy.modules.email.adapter.web.mapper.validate;

import org.springframework.stereotype.Component;

import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
import com.dataracy.modules.email.adapter.web.response.password.GetResetTokenWebResponse;
import com.dataracy.modules.email.application.dto.request.validate.VerifyCodeRequest;
import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;

/** 이메일 웹 DTO와 이메일 도메인 DTO를 서로 변환하기 위한 매퍼 */
@Component
public class ValidateEmailWebMapper {
  /**
   * 이메일 인증 코드 검증 웹 요청 DTO를 애플리케이션 요청 DTO로 변환합니다.
   *
   * @param webRequest 이메일 인증 코드 검증에 필요한 웹 요청 DTO
   * @return 변환된 애플리케이션 계층의 이메일 인증 코드 검증 요청 DTO
   */
  public VerifyCodeRequest toApplicationDto(VerifyCodeWebRequest webRequest) {
    return new VerifyCodeRequest(webRequest.email(), webRequest.code(), webRequest.purpose());
  }

  /**
   * 애플리케이션 계층의 이메일 인증 코드 검증 응답 DTO를 이메일 인증 코드 검증 웹 응답 DTO로 변환합니다.
   *
   * @param responseDto 변환할 비밀번호 재설정용 애플리케이션 응답 DTO
   * @return 변환된 비밀번호 재설정용 토큰 웹 응답 DTO
   */
  public GetResetTokenWebResponse toWebDto(GetResetTokenResponse responseDto) {
    return new GetResetTokenWebResponse(responseDto.resetToken());
  }
}
