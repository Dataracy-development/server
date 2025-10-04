package com.dataracy.modules.user.adapter.web.api.password;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ResetPasswordWithTokenWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User - Password", description = "사용자 관련 API - 비밀번호")
@RequestMapping("/api/v1")
public interface UserPasswordApi {
  /**
   * 현재 인증된 사용자의 비밀번호를 변경한다.
   *
   * @param webRequest 변경할 비밀번호 정보가 포함된 요청 객체
   * @return 비밀번호 변경 성공 시 성공 응답을 반환한다.
   */
  @Operation(summary = "비밀번호를 변경한다.", description = "비밀번호를 변경한다.")
  @Parameter(
      in = ParameterIn.HEADER,
      name = "Authorization",
      required = true,
      schema = @Schema(type = "string"),
      description = "Bearer [Access 토큰]")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "비밀번호 변경에 성공했습니다.",
            useReturnTypeSchema = true),
      })
  @PutMapping(value = "/user/password/change")
  ResponseEntity<SuccessResponse<Void>> changePassword(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "변경할 비밀번호",
              content = @Content(schema = @Schema(implementation = ChangePasswordWebRequest.class)))
          @Validated
          @RequestBody
          ChangePasswordWebRequest webRequest);

  /**
   * 토큰을 이용해 비로그인 사용자의 비밀번호를 재설정한다.
   *
   * @param webRequest 비밀번호 재설정 토큰과 새로운 비밀번호 정보를 포함한 요청 객체
   * @return 비밀번호 재설정 성공 시 성공 응답을 반환한다.
   */
  @Operation(
      summary = "비밀번호를 재설정한다.",
      description = "비로그인 시 비밀번호를 재설정하는 API입니다.",
      security = {})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "비밀번호 재설정에 성공했습니다.",
            useReturnTypeSchema = true),
      })
  @PutMapping(value = "/password/reset")
  ResponseEntity<SuccessResponse<Void>> resetPasswordWithToken(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "변경할 비밀번호",
              content =
                  @Content(
                      schema = @Schema(implementation = ResetPasswordWithTokenWebRequest.class)))
          @Validated
          @RequestBody
          ResetPasswordWithTokenWebRequest webRequest);

  /**
   * 현재 사용자의 비밀번호가 입력한 값과 일치하는지 검증한다.
   *
   * @param webRequest 확인할 비밀번호 정보를 포함한 요청 객체
   * @return 비밀번호가 일치할 경우 성공 응답을 반환한다.
   */
  @Operation(summary = "비밀번호를 확인한다.", description = "비밀번호 일치 여부를 확인한다.")
  @Parameter(
      in = ParameterIn.HEADER,
      name = "Authorization",
      required = true,
      schema = @Schema(type = "string"),
      description = "Bearer [Access 토큰]")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "비밀번호 확인에 성공했습니다.",
            useReturnTypeSchema = true),
      })
  @PostMapping(value = "/user/password/confirm")
  ResponseEntity<SuccessResponse<Void>> confirmPassword(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "확인할 비밀번호",
              content =
                  @Content(schema = @Schema(implementation = ConfirmPasswordWebRequest.class)))
          @Validated
          @RequestBody
          ConfirmPasswordWebRequest webRequest);
}
