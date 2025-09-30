package com.dataracy.modules.email.domain.exception;

import com.dataracy.modules.common.status.BaseErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EmailException 테스트")
class EmailExceptionTest {

    @Test
    @DisplayName("EmailException 생성 및 속성 확인")
    void emailException_ShouldHaveCorrectProperties() {
        // Given
        BaseErrorCode errorCode = new BaseErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public String getCode() {
                return "EMAIL_001";
            }

            @Override
            public String getMessage() {
                return "이메일 전송에 실패했습니다.";
            }
        };

        // When
        EmailException exception = new EmailException(errorCode);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getCode()).isEqualTo("EMAIL_001");
        assertThat(exception.getMessage()).isEqualTo("이메일 전송에 실패했습니다.");
    }

    @Test
    @DisplayName("EmailException은 BusinessException을 상속받는다")
    void emailException_ShouldExtendBusinessException() {
        // Given
        BaseErrorCode errorCode = new BaseErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }

            @Override
            public String getCode() {
                return "EMAIL_002";
            }

            @Override
            public String getMessage() {
                return "이메일 서비스가 일시적으로 사용할 수 없습니다.";
            }
        };

        // When
        EmailException exception = new EmailException(errorCode);

        // Then
        assertThat(exception).isInstanceOf(com.dataracy.modules.common.exception.BusinessException.class);
        assertThat(exception).isInstanceOf(com.dataracy.modules.common.exception.CustomException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
