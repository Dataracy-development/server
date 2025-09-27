package com.dataracy.modules.common.exception;

import com.dataracy.modules.common.status.CommonErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CustomException 테스트")
class CustomExceptionTest {

    @Test
    @DisplayName("CustomException 생성 및 기본 메서드 테스트")
    void customExceptionCreationAndMethods() {
        // given
        CustomException exception = new CustomException(CommonErrorStatus.BAD_REQUEST);

        // when & then
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getCode()).isEqualTo("COMMON-400");
        assertThat(exception.getMessage()).isEqualTo("잘못된 요청입니다.");
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("CustomException - INTERNAL_SERVER_ERROR")
    void customExceptionInternalServerError() {
        // given
        CustomException exception = new CustomException(CommonErrorStatus.INTERNAL_SERVER_ERROR);

        // when & then
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(exception.getCode()).isEqualTo("COMMON-500");
        assertThat(exception.getMessage()).isEqualTo("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.");
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("CustomException - NOT_FOUND_HANDLER")
    void customExceptionNotFoundHandler() {
        // given
        CustomException exception = new CustomException(CommonErrorStatus.NOT_FOUND_HANDLER);

        // when & then
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getCode()).isEqualTo("COMMON-404");
        assertThat(exception.getMessage()).isEqualTo("요청 경로를 찾을 수 없습니다.");
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.NOT_FOUND_HANDLER);
    }
}
