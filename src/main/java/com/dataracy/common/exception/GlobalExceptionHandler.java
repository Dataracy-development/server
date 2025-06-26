package com.dataracy.common.exception;

import com.dataracy.common.dto.ErrorResponse;
import com.dataracy.common.status.CommonErrorStatus;
import com.dataracy.common.status.CommonException;
import com.dataracy.user.status.UserException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 커스텀 비즈니스(도메인) 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(BusinessException e) {
        if (e instanceof UserException) {
            String errorMessage = "유저 도메인 예외입니다: " + e.getMessage();
            logException("UserException", errorMessage, e);
        } else {
            String errorMessage = "공통 예외입니다: " + e.getMessage();
            logException("CustomException", errorMessage, e);
        }
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    // 커스텀 글로벌 예외 처리
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(CommonException e) {
        String errorMessage = "공통 글로벌 예외입니다: " + e.getMessage();
        logException("CommonException", errorMessage, e);

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    // Security 인증 관련 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
        String errorMessage = "인증 예외입니다: " + e.getMessage();
        logException("SecurityException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(CommonErrorStatus.UNAUTHORIZED, errorMessage));
    }

    // IllegalArgumentException 처리 (잘못된 인자가 전달된 경우)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        String errorMessage = "잘못된 요청입니다: " + e.getMessage();
        logException("IllegalArgumentException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        String errorMessage = "요청을 처리하는 중에 Null 값이 참조되었습니다.: " + e.getMessage();
        logException("NullPointerException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR, errorMessage));
    }

    // NumberFormatException 처리
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponse> handleNumberFormatException(NumberFormatException e) {
        String errorMessage = "숫자 형식이 잘못되었습니다: " + e.getMessage();
        logException("NumberFormatException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // IndexOutOfBoundsException 처리
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<ErrorResponse> handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
        String errorMessage = "인덱스가 범위를 벗어났습니다: " + e.getMessage();
        logException("IndexOutOfBoundsException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // ConstraintViolationException 처리 (쿼리 파라미터에 올바른 값이 들어오지 않은 경우)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationParameterException(ConstraintViolationException e) {
        String errorMessage = "잘못된 쿼리 값입니다: " + e.getMessage();
        logException("ConstraintViolationException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // MissingRequestHeaderException 처리 (필수 헤더가 누락된 경우)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        String errorMessage = "필수 헤더 '" + e.getHeaderName() + "'가 없습니다.";
        logException("MissingRequestHeaderException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // DataIntegrityViolationException 처리 (데이터베이스 제약 조건 위반)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String errorMessage = "데이터 무결성 제약 조건을 위반했습니다: " + e.getMessage();
        logException("DataIntegrityViolationException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // MissingServletRequestParameterException 처리 (필수 쿼리 파라미터가 입력되지 않은 경우)
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException e,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        String errorMessage = "필수 파라미터 '" + e.getParameterName() + "'가 없습니다.";
        logException("MissingServletRequestParameterException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // MethodArgumentNotValidException 처리 (RequestBody로 들어온 필드들의 유효성 검증에 실패한 경우)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String combinedErrors = extractFieldErrors(e.getBindingResult().getFieldErrors());
        String errorMessage = "필드의 유효성 검증에 실패했습니다: " + combinedErrors;
        logException("ValidationError", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // NoHandlerFoundException 처리 (요청 경로에 매핑된 핸들러가 없는 경우)
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException e,
                                                                   HttpHeaders headers,
                                                                   HttpStatusCode status,
                                                                   WebRequest request) {
        String errorMessage = "해당 경로에 대한 핸들러를 찾을 수 없습니다: " + e.getRequestURL();
        logException("NoHandlerFoundException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(CommonErrorStatus.NOT_FOUND_HANDLER, errorMessage));
    }

    // HttpRequestMethodNotSupportedException 처리 (지원하지 않는 HTTP 메소드 요청이 들어온 경우)
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {
        String errorMessage = "지원하지 않는 HTTP 메소드 요청입니다: " + e.getMethod();
        logException("HttpRequestMethodNotSupportedException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(CommonErrorStatus.METHOD_NOT_ALLOWED, errorMessage));
    }

    // HttpMediaTypeNotSupportedException 처리 (지원하지 않는 미디어 타입 요청이 들어온 경우)
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode status,
                                                                     WebRequest request) {
        String errorMessage = "지원하지 않는 미디어 타입입니다: " + e.getContentType();
        logException("HttpMediaTypeNotSupportedException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.of(CommonErrorStatus.UNSUPPORTED_MEDIA_TYPE, errorMessage));
    }

    // HttpMessageNotReadableException 처리 (잘못된 JSON 형식)
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
                                                               WebRequest request) {
        String errorMessage = "요청 본문을 읽을 수 없습니다. 올바른 JSON 형식이어야 합니다: " + e.getMessage();
        logException("HttpMessageNotReadableException", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST, errorMessage));
    }

    // 내부 서버 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        String errorMessage = "내부 서버 오류입니다: " + e.getMessage();
        logException("Exception", errorMessage, e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR, errorMessage));
    }

    // 유효성 검증 오류 메시지 추출 메서드 (FieldErrors)
    private String extractFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    // 로그 기록 메서드
    private void logException(String label, String message, Exception e) {
        if (message.equals("IllegalArgumentException")
                || message.equals("NullPointerException")
                || message.equals("DataIntegrityViolationException")
                || message.equals("ConstraintViolationException")
                || message.equals("MissingRequestHeaderException")
                || message.equals("MissingServletRequestParameterException")
                || message.equals("MethodArgumentNotValidException")
                || message.equals("NumberFormatException")
                || message.equals("HttpMessageNotReadableException")
                || message.equals("HttpRequestMethodNotSupportedException")
                || message.equals("HttpMediaTypeNotSupportedException")
                || message.equals("NoHandlerFoundException")) {
            log.warn("[{}] : {}", label, message, e);
        } else {
            log.error("[{}] : {}", label, message, e);
        }
    }
}
