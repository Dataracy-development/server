/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.security.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.dto.response.ErrorResponse;
import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;
import com.dataracy.modules.common.status.CommonErrorStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 서비스와 관련된 에러사항은 ControllerAdvice에서 처리할 수 있지만 securityContext내의 에러사항은 시큐리티의 exceptionHandling에서
 * 처리하기 때문에 CustomAuthenticationEntryPoint를 통해 에러처리를 진행한다.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  /**
   * JSON 직렬화를 위한 ObjectMapper를 사용하여 CustomAuthenticationEntryPoint를 생성합니다.
   *
   * @param objectMapper 에러 응답을 JSON으로 변환하는 데 사용되는 ObjectMapper 인스턴스
   */
  public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Spring Security 인증 과정에서 발생한 예외를 감지하여 JSON 형식의 에러 응답을 반환한다.
   *
   * <p>인증 필터 체인에서 발생한 예외가 BusinessException인 경우 해당 에러 코드에 맞는 응답과 HTTP 상태 코드를 반환하며, 그 외의 예외는 내부 서버
   * 오류(500)로 처리하여 표준 에러 응답을 반환한다.
   *
   * @param request 인증 과정에서 발생한 예외가 포함된 HTTP 요청
   * @param response 에러 정보를 담아 반환할 HTTP 응답
   * @param authException 인증 실패 시 발생한 예외
   * @throws IOException 응답 작성 중 입출력 오류가 발생할 경우
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    Object ex = request.getAttribute("filter.error");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (ex instanceof BusinessException businessEx) {
      BaseErrorCode errorCode = businessEx.getErrorCode();
      ErrorResponse errorResponse = ErrorResponse.of(errorCode);
      response.setStatus(errorResponse.getHttpStatus());
      objectMapper.writeValue(response.getWriter(), errorResponse);
    } else {
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      ErrorResponse errorResponse = ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR);
      objectMapper.writeValue(response.getWriter(), errorResponse);
    }
  }
}
