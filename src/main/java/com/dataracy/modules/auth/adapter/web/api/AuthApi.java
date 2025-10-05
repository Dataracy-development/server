package com.dataracy.modules.auth.adapter.web.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.common.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Auth - Prod", description = "인증 관련 실제 운영용 API")
@RequestMapping("/api/v1/auth")
public interface AuthApi {
  /**
   * 이메일과 비밀번호를 이용해 자체 로그인을 수행하고, 성공 시 리프레시 토큰을 쿠키에 저장한다.
   *
   * @param webRequest 로그인에 필요한 이메일과 비밀번호가 포함된 요청 객체
   * @return 로그인 성공 여부를 나타내는 응답 객체
   */
  @Operation(
      summary = "자체 로그인 - 이전버전",
      description = "자체 로그인(email, password)을 통해 로그인합니다.",
      security = {})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "자체로그인 성공", useReturnTypeSchema = true)
      })
  @PostMapping(value = "/login-before", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<SuccessResponse<Void>> loginBefore(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "로그인 정보 (이메일, 비밀번호)",
              content = @Content(schema = @Schema(implementation = SelfLoginWebRequest.class)))
          @Validated
          @RequestBody
          SelfLoginWebRequest webRequest,
      @Parameter(hidden = true) HttpServletRequest request,
      @Parameter(hidden = true) HttpServletResponse response);

  /**
   * 이메일과 비밀번호를 이용해 자체 로그인을 수행하고, 성공 시 리프레시 토큰을 쿠키에 저장한다.
   *
   * @param webRequest 로그인에 필요한 이메일과 비밀번호가 포함된 요청 객체
   * @return 로그인 성공 여부를 나타내는 응답 객체
   */
  @Operation(
      summary = "자체 로그인",
      description = "자체 로그인(email, password)을 통해 로그인합니다.",
      security = {})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "자체로그인 성공", useReturnTypeSchema = true)
      })
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<SuccessResponse<Void>> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "로그인 정보 (이메일, 비밀번호)",
              content = @Content(schema = @Schema(implementation = SelfLoginWebRequest.class)))
          @Validated
          @RequestBody
          SelfLoginWebRequest webRequest,
      @Parameter(hidden = true) HttpServletRequest request,
      @Parameter(hidden = true) HttpServletResponse response);

  /**
   * 리프레시 토큰을 이용해 새로운 액세스 토큰과 리프레시 토큰을 발급하고, 이를 쿠키에 저장합니다.
   *
   * @param refreshToken 쿠키에서 추출한 리프레시 토큰 문자열
   * @return 토큰 재발급 성공 시 성공 응답을 반환합니다.
   */
  @Operation(
      summary = "토큰 재발급",
      description = "쿠키의 리프레시 토큰을 통해 토큰 재발급을 시행한다.",
      security = {})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", useReturnTypeSchema = true)
      })
  @PostMapping(value = "/token/re-issue")
  ResponseEntity<SuccessResponse<Void>> reIssueToken(
      @Parameter(
              in = ParameterIn.COOKIE,
              name = "refreshToken",
              required = true,
              description = "리프레시 토큰으로 토큰 재발급을 진행한다.",
              schema = @Schema(type = "string"))
          @CookieValue(value = "refreshToken")
          String refreshToken,
      @Parameter(hidden = true) HttpServletRequest request,
      @Parameter(hidden = true) HttpServletResponse response);
}
