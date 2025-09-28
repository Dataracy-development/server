package com.dataracy.modules.common.exception;

import com.dataracy.modules.common.status.BaseErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommonException 테스트")
class CommonExceptionTest {

    @Test
    @DisplayName("CommonException 생성 및 속성 확인")
    void commonException_ShouldHaveCorrectProperties() {
        // Given
        BaseErrorCode errorCode = new BaseErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public String getCode() {
                return "COMMON_001";
            }

            @Override
            public String getMessage() {
                return "공통 오류가 발생했습니다.";
            }
        };

        // When
        CommonException exception = new CommonException(errorCode);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(exception.getCode()).isEqualTo("COMMON_001");
        assertThat(exception.getMessage()).isEqualTo("공통 오류가 발생했습니다.");
    }

    @Test
    @DisplayName("CommonException은 CustomException을 상속받는다")
    void commonException_ShouldExtendCustomException() {
        // Given
        BaseErrorCode errorCode = new BaseErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public String getCode() {
                return "COMMON_002";
            }

            @Override
            public String getMessage() {
                return "잘못된 요청입니다.";
            }
        };

        // When
        CommonException exception = new CommonException(errorCode);

        // Then
        assertThat(exception).isInstanceOf(CustomException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
