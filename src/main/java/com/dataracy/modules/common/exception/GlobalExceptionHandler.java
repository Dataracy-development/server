/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.dataracy.modules.common.dto.response.ErrorResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.support.lock.LockAcquisitionException;
import com.dataracy.modules.security.exception.SecurityException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // Exception 상수 정의
  private static final String COMMON_EXCEPTION = "CommonException";
  private static final String LOCK_EXCEPTION = "LockException";
  private static final String SECURITY_EXCEPTION = "SecurityException";
  private static final String ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException";
  private static final String NULL_POINTER_EXCEPTION = "NullPointerException";
  private static final String NUMBER_FORMAT_EXCEPTION = "NumberFormatException";
  private static final String INDEX_OUT_OF_BOUNDS_EXCEPTION = "IndexOutOfBoundsException";
  private static final String CONSTRAINT_VIOLATION_EXCEPTION = "ConstraintViolationException";
  private static final String MISSING_REQUEST_HEADER_EXCEPTION = "MissingRequestHeaderException";
  private static final String DATA_INTEGRITY_VIOLATION_EXCEPTION =
      "DataIntegrityViolationException";
  private static final String MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION =
      "MissingServletRequestParameterException";
  private static final String VALIDATION_EXCEPTION = "ValidationException";
  private static final String NO_HANDLER_FOUND_EXCEPTION = "NoHandlerFoundException";
  private static final String HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION =
      "HttpRequestMethodNotSupportedException";
  private static final String HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION =
      "HttpMediaTypeNotSupportedException";
  private static final String HTTP_MESSAGE_NOT_READABLE_EXCEPTION =
      "HttpMessageNotReadableException";

  /**
   * 비즈니스 예외를 처리하여 해당 HTTP 상태 코드와 표준화된 에러 응답을 반환합니다.
   *
   * @param e 처리할 비즈니스 예외 객체
   * @return 예외에 정의된 HTTP 상태 코드와 에러 코드가 포함된 ErrorResponse
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(BusinessException e) {
    return ResponseEntity.status(e.getHttpStatus()).body(ErrorResponse.of(e.getErrorCode()));
  }

  /**
   * CommonException을 처리하여 표준화된 에러 응답을 반환합니다.
   *
   * @param e 처리할 CommonException 인스턴스
   * @return 예외에 정의된 HTTP 상태 코드와 에러 코드가 포함된 ErrorResponse
   */
  @ExceptionHandler(CommonException.class)
  public ResponseEntity<ErrorResponse> handleCommonException(CommonException e) {
    LoggerFactory.common().logError(COMMON_EXCEPTION, "공통 글로벌 예외입니다.", e);
    return ResponseEntity.status(e.getHttpStatus()).body(ErrorResponse.of(e.getErrorCode()));
  }

  /**
   * 동시성 락 획득 실패 예외를 처리하여 HTTP 409(CONFLICT) 응답을 반환합니다.
   *
   * @return 동시성 충돌이 발생했음을 나타내는 에러 응답
   */
  @ExceptionHandler(LockAcquisitionException.class)
  public ResponseEntity<ErrorResponse> handleLockError(LockAcquisitionException e) {
    LoggerFactory.common().logError(LOCK_EXCEPTION, "동시성 락 예외입니다.", e);
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorResponse.of(CommonErrorStatus.CONFLICT, e.getMessage()));
  }

  /**
   * SecurityException이 발생할 때 인증 실패에 대한 401 Unauthorized 응답을 반환합니다.
   *
   * @return 인증 실패 시 401 상태 코드와 에러 메시지를 포함한 ErrorResponse 객체
   */
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
    LoggerFactory.common().logError(SECURITY_EXCEPTION, "인증 예외입니다.", e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.of(CommonErrorStatus.UNAUTHORIZED, e.getMessage()));
  }

  /**
   * 잘못된 인자가 전달된 경우 발생하는 IllegalArgumentException을 처리합니다. 클라이언트가 유효하지 않은 파라미터를 전달했을 때 HTTP 400 Bad
   * Request와 함께 에러 메시지를 반환합니다.
   *
   * @return HTTP 400 상태 코드와 에러 응답 본문
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    LoggerFactory.common().logError(ILLEGAL_ARGUMENT_EXCEPTION, "잘못된 인자가 전달되었습니다.", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * NullPointerException이 발생했을 때 500 내부 서버 오류와 함께 표준 에러 응답을 반환합니다.
   *
   * @return 내부 서버 오류 상태와 에러 메시지를 포함한 ErrorResponse
   */
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
    LoggerFactory.common().logError(NULL_POINTER_EXCEPTION, "요청을 처리하는 중에 Null 값이 참조되었습니다.", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
  }

  /**
   * 잘못된 숫자 형식의 입력이 발생했을 때 HTTP 400 Bad Request 응답을 반환합니다.
   *
   * @return 숫자 형식 오류에 대한 에러 응답
   */
  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<ErrorResponse> handleNumberFormatException(NumberFormatException e) {
    LoggerFactory.common().logError(NUMBER_FORMAT_EXCEPTION, "숫자 형식이 잘못되었습니다.", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * IndexOutOfBoundsException이 발생했을 때 HTTP 400 Bad Request와 에러 메시지를 반환합니다.
   *
   * @return 잘못된 인덱스 접근 시의 에러 응답
   */
  @ExceptionHandler(IndexOutOfBoundsException.class)
  public ResponseEntity<ErrorResponse> handleIndexOutOfBoundsException(
      IndexOutOfBoundsException e) {
    LoggerFactory.common().logError(INDEX_OUT_OF_BOUNDS_EXCEPTION, "인덱스가 범위를 벗어났습니다.", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * 쿼리 파라미터의 유효성 검사 실패 시 400 Bad Request 응답을 반환합니다.
   *
   * @return 잘못된 쿼리 값에 대한 에러 응답
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleValidationParameterException(
      ConstraintViolationException e) {
    LoggerFactory.common().logError(CONSTRAINT_VIOLATION_EXCEPTION, "잘못된 쿼리 값입니다.", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * 필수 HTTP 요청 헤더가 누락된 경우 400 Bad Request 응답을 반환합니다. 누락된 헤더 이름과 함께 오류 메시지를 포함한 표준 에러 응답을 제공합니다.
   *
   * @param e 누락된 요청 헤더 예외
   * @return 400 상태 코드와 에러 정보를 담은 ResponseEntity
   */
  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(
      MissingRequestHeaderException e) {
    LoggerFactory.common()
        .logError(MISSING_REQUEST_HEADER_EXCEPTION, "필수 헤더 '" + e.getHeaderName() + "'가 없습니다.", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * 데이터베이스 무결성 제약 조건 위반 예외를 처리합니다. 데이터 무결성 제약 조건이 위반될 경우 HTTP 400 Bad Request와 함께 오류 응답을 반환합니다.
   *
   * @return 데이터 무결성 위반에 대한 오류 응답
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException e) {
    LoggerFactory.common().logError(DATA_INTEGRITY_VIOLATION_EXCEPTION, "데이터 무결성 제약 조건을 위반했습니다", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * 필수 쿼리 파라미터가 누락된 경우 400 Bad Request 응답을 반환합니다.
   *
   * @param e 누락된 파라미터 정보를 포함한 예외 객체
   * @return 400 상태 코드와 에러 메시지를 포함한 응답
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    LoggerFactory.common()
        .logError(
            MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION,
            "필수 파라미터 '" + e.getParameterName() + "'가 없습니다.",
            e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * RequestBody로 전달된 필드의 유효성 검증에 실패한 경우 400 Bad Request와 표준 에러 응답을 반환합니다.
   *
   * @param e 유효성 검증에 실패한 필드 정보를 포함한 예외
   * @param headers HTTP 헤더 정보
   * @param status HTTP 상태 코드
   * @param request 웹 요청 정보
   * @return 400 Bad Request와 에러 메시지를 포함한 표준 에러 응답
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String combinedErrors = extractFieldErrors(e.getBindingResult().getFieldErrors());
    LoggerFactory.common()
        .logError(VALIDATION_EXCEPTION, "필드의 유효성 검증에 실패했습니다: " + combinedErrors, e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, combinedErrors));
  }

  /**
   * 요청 경로에 매핑된 핸들러가 없을 때 404 Not Found 응답을 반환합니다. 요청된 URL에 해당하는 컨트롤러가 존재하지 않을 경우 호출되며, 표준화된 에러 응답을
   * 제공합니다.
   *
   * @return 404 상태 코드와 NOT_FOUND_HANDLER 에러 정보를 포함한 응답
   */
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    LoggerFactory.common()
        .logError(NO_HANDLER_FOUND_EXCEPTION, "해당 경로에 대한 핸들러를 찾을 수 없습니다: " + e.getRequestURL(), e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(CommonErrorStatus.NOT_FOUND_HANDLER, e.getMessage()));
  }

  /**
   * 지원하지 않는 HTTP 메소드 요청이 들어왔을 때 405 Method Not Allowed 응답을 반환합니다.
   *
   * @param e 지원되지 않는 HTTP 메소드 예외
   * @param headers HTTP 헤더 정보
   * @param status HTTP 상태 코드
   * @param request 웹 요청 정보
   * @return 405 Method Not Allowed와 에러 메시지를 포함한 응답
   */
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    LoggerFactory.common()
        .logError(
            HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION,
            "지원하지 않는 HTTP 메소드 요청입니다: " + e.getMethod(),
            e);
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(ErrorResponse.of(CommonErrorStatus.METHOD_NOT_ALLOWED, e.getMessage()));
  }

  /**
   * 지원하지 않는 미디어 타입 요청이 들어올 때 415 응답과 표준 에러 메시지를 반환합니다.
   *
   * @return 415 Unsupported Media Type와 에러 응답 본문
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    LoggerFactory.common()
        .logError(
            HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION, "지원하지 않는 미디어 타입입니다: " + e.getContentType(), e);
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body(ErrorResponse.of(CommonErrorStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage()));
  }

  /**
   * 읽을 수 없는 HTTP 메시지(예: 잘못된 JSON 형식) 예외를 처리합니다. 잘못된 요청 본문으로 인해 역직렬화에 실패한 경우 400 Bad Request와 표준 에러
   * 응답을 반환합니다.
   *
   * @return 400 Bad Request와 에러 메시지를 포함한 응답
   */
  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    LoggerFactory.common()
        .logError(HTTP_MESSAGE_NOT_READABLE_EXCEPTION, "요청 본문을 읽을 수 없습니다. 올바른 JSON 형식이어야 합니다.", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, e.getMessage()));
  }

  /**
   * 모든 처리되지 않은 예외를 내부 서버 오류로 처리하여 표준화된 에러 응답을 반환합니다.
   *
   * @param e 처리되지 않은 예외
   * @return HTTP 500 상태 코드와 함께 내부 서버 오류 정보를 담은 ErrorResponse 객체
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    LoggerFactory.common().logError("Exception", "내부 서버 오류입니다.", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
  }

  /**
   * 주어진 필드 오류 목록에서 각 오류 메시지를 추출하여 하나의 문자열로 합칩니다.
   *
   * @param fieldErrors 유효성 검증에 실패한 필드 오류 목록
   * @return 각 필드 오류 메시지를 쉼표로 구분하여 연결한 문자열
   */
  private String extractFieldErrors(List<FieldError> fieldErrors) {
    return fieldErrors.stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(", "));
  }
}
