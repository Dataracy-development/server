package com.dataracy.modules.security.exception;

import com.dataracy.modules.common.status.BaseErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("SecurityException 테스트")
class SecurityExceptionTest {

    @Test
    @DisplayName("SecurityException 생성 및 속성 확인")
    void securityException_ShouldHaveCorrectProperties() {
        // Given
        BaseErrorCode errorCode = new BaseErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.UNAUTHORIZED;
            }

            @Override
            public String getCode() {
                return "SECURITY_001";
            }

            @Override
            public String getMessage() {
                return "보안 위반이 감지되었습니다.";
            }
        };

        // When
        SecurityException exception = new SecurityException(errorCode);

        // Then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(exception.getCode()).isEqualTo("SECURITY_001"),
                () -> assertThat(exception.getMessage()).isEqualTo("보안 위반이 감지되었습니다.")
        );
    }

    @Test
    @DisplayName("SecurityException은 BusinessException을 상속받는다")
    void securityException_ShouldExtendBusinessException() {
        // Given
        BaseErrorCode errorCode = new BaseErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.FORBIDDEN;
            }

            @Override
            public String getCode() {
                return "SECURITY_002";
            }

            @Override
            public String getMessage() {
                return "접근이 거부되었습니다.";
            }
        };

        // When
        SecurityException exception = new SecurityException(errorCode);

        // Then
        assertThat(exception)
                .isInstanceOf(com.dataracy.modules.common.exception.BusinessException.class)
                .isInstanceOf(com.dataracy.modules.common.exception.CustomException.class)
                .isInstanceOf(RuntimeException.class);
    }
}
