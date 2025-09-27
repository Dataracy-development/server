package com.dataracy.modules.common.dto.response;

import com.dataracy.modules.common.status.BaseErrorCode;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    @DisplayName("of(BaseErrorCode) - 기본 에러 응답 생성")
    void of_WithErrorCode_CreatesErrorResponse() {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;

        // when
        ErrorResponse response = ErrorResponse.of(errorCode);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("of(BaseErrorCode, String) - 커스텀 메시지와 함께 에러 응답 생성")
    void of_WithCustomMessage_CreatesErrorResponse() {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;
        String customMessage = "Custom error message";

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, customMessage);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("of(BaseErrorCode) - 500 에러 응답 생성")
    void of_InternalServerError_CreatesErrorResponse() {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.INTERNAL_SERVER_ERROR;

        // when
        ErrorResponse response = ErrorResponse.of(errorCode);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("of(BaseErrorCode, String) - 404 에러 응답 생성")
    void of_NotFoundError_CreatesErrorResponse() {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.NOT_FOUND_HANDLER;
        String customMessage = "Resource not found";

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, customMessage);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("builder() - 빌더 패턴으로 에러 응답 생성")
    void builder_CreatesErrorResponse() {
        // when
        ErrorResponse response = ErrorResponse.builder()
                .httpStatus(400)
                .code("BAD_REQUEST")
                .message("Invalid request parameters")
                .build();

        // then
        assertThat(response.getHttpStatus()).isEqualTo(400);
        assertThat(response.getCode()).isEqualTo("BAD_REQUEST");
        assertThat(response.getMessage()).isEqualTo("Invalid request parameters");
    }

    @Test
    @DisplayName("of(BaseErrorCode) - null 메시지 처리")
    void of_WithNullMessage_CreatesErrorResponse() {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, null);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isNull();
    }

    @Test
    @DisplayName("of(BaseErrorCode) - 빈 문자열 메시지 처리")
    void of_WithEmptyMessage_CreatesErrorResponse() {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;
        String emptyMessage = "";

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, emptyMessage);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(emptyMessage);
    }

    @Test
    @DisplayName("of(BaseErrorCode) - 다양한 HTTP 상태 코드 테스트")
    void of_VariousHttpStatusCodes_CreatesErrorResponse() {
        // given
        BaseErrorCode badRequest = CommonErrorStatus.BAD_REQUEST;
        BaseErrorCode notFound = CommonErrorStatus.NOT_FOUND_HANDLER;
        BaseErrorCode internalError = CommonErrorStatus.INTERNAL_SERVER_ERROR;

        // when
        ErrorResponse badRequestResponse = ErrorResponse.of(badRequest);
        ErrorResponse notFoundResponse = ErrorResponse.of(notFound);
        ErrorResponse internalErrorResponse = ErrorResponse.of(internalError);

        // then
        assertThat(badRequestResponse.getHttpStatus()).isEqualTo(400);
        assertThat(notFoundResponse.getHttpStatus()).isEqualTo(404);
        assertThat(internalErrorResponse.getHttpStatus()).isEqualTo(500);
    }

    @Test
    @DisplayName("of(BaseErrorCode, String) - 긴 메시지 처리")
    void of_WithLongMessage_CreatesErrorResponse() {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;
        String longMessage = "This is a very long error message that contains detailed information about what went wrong during the request processing and should be handled properly by the error response system";

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, longMessage);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(longMessage);
    }
}
