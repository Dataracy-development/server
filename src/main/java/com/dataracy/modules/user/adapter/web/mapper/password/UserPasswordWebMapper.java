package com.dataracy.modules.user.adapter.web.mapper.password;

import org.springframework.stereotype.Component;

import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ResetPasswordWithTokenWebRequest;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;

/** 유저 비밀번호 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class UserPasswordWebMapper {
  /**
   * 비밀번호 변경 웹 요청 DTO를 애플리케이션 계층의 비밀번호 변경 요청 DTO로 변환합니다.
   *
   * @param webRequest 비밀번호와 비밀번호 확인 값을 포함하는 웹 요청 DTO
   * @return 비밀번호와 비밀번호 확인 값을 담은 비밀번호 변경 요청 DTO
   */
  public ChangePasswordRequest toApplicationDto(ChangePasswordWebRequest webRequest) {
    return new ChangePasswordRequest(webRequest.password(), webRequest.passwordConfirm());
  }

  /**
   * ResetPasswordWithTokenWebRequest 객체를 ResetPasswordWithTokenRequest 애플리케이션 DTO로 변환합니다.
   *
   * @param webRequest 비밀번호 재설정 토큰, 비밀번호, 비밀번호 확인 값을 포함한 웹 요청 DTO
   * @return 변환된 ResetPasswordWithTokenRequest 애플리케이션 DTO
   */
  public ResetPasswordWithTokenRequest toApplicationDto(
      ResetPasswordWithTokenWebRequest webRequest) {
    return new ResetPasswordWithTokenRequest(
        webRequest.resetPasswordToken(), webRequest.password(), webRequest.passwordConfirm());
  }

  /**
   * 비밀번호 확인 웹 요청 DTO를 비밀번호 확인 애플리케이션 요청 DTO로 변환합니다.
   *
   * @param webRequest 비밀번호 확인 정보를 담고 있는 웹 요청 DTO
   * @return 변환된 비밀번호 확인 애플리케이션 요청 DTO
   */
  public ConfirmPasswordRequest toApplicationDto(ConfirmPasswordWebRequest webRequest) {
    return new ConfirmPasswordRequest(webRequest.password());
  }
}
