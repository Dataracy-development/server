package com.dataracy.modules.common.exception;

import com.dataracy.modules.common.dto.response.ErrorResponse;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.support.lock.LockAcquisitionException;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.security.exception.SecurityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Nested
    @DisplayName("비즈니스 예외 처리 테스트")
    class BusinessExceptionHandlingTest {

        @Test
        @DisplayName("성공: BusinessException을 올바른 HTTP 상태 코드로 처리한다")
        void success_handleBusinessException() {
            // given
            DataException dataException = new DataException(DataErrorStatus.NOT_FOUND_DATA);

            // when
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomException(dataException);

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().getCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA.getCode()),
                    () -> assertThat(response.getBody().getMessage()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA.getMessage())
            );
        }

        @Test
        @DisplayName("성공: CommonException을 올바른 HTTP 상태 코드로 처리한다")
        void success_handleCommonException() {
            // given
            CommonException commonException = new CommonException(CommonErrorStatus.INTERNAL_SERVER_ERROR);

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleCommonException(commonException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.INTERNAL_SERVER_ERROR.getCode()),
                        () -> assertThat(response.getBody().getMessage()).isEqualTo(CommonErrorStatus.INTERNAL_SERVER_ERROR.getMessage())
                );
                
                verify(mockLogger).logError("CommonException", "공통 글로벌 예외입니다.", commonException);
            }
        }
    }

    @Nested
    @DisplayName("동시성 락 예외 처리 테스트")
    class LockExceptionHandlingTest {

        @Test
        @DisplayName("성공: LockAcquisitionException을 HTTP 409로 처리한다")
        void success_handleLockAcquisitionException() {
            // given
            LockAcquisitionException lockException = new LockAcquisitionException("락 획득 실패");

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleLockError(lockException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.CONFLICT.getCode()),
                        () -> assertThat(response.getBody().getMessage()).isEqualTo("락 획득 실패")
                );
                
                verify(mockLogger).logError("LockException", "동시성 락 예외입니다.", lockException);
            }
        }
    }

    @Nested
    @DisplayName("보안 예외 처리 테스트")
    class SecurityExceptionHandlingTest {

        @Test
        @DisplayName("성공: SecurityException을 HTTP 401로 처리한다")
        void success_handleSecurityException() {
            // given
            SecurityException securityException = mock(SecurityException.class);
            when(securityException.getMessage()).thenReturn("인증 실패");

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleSecurityException(securityException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.UNAUTHORIZED.getCode()),
                        () -> assertThat(response.getBody().getMessage()).isEqualTo("인증 실패")
                );
                
                verify(mockLogger).logError("SecurityException", "인증 예외입니다.", securityException);
            }
        }
    }

    @Nested
    @DisplayName("잘못된 인자 예외 처리 테스트")
    class IllegalArgumentHandlingTest {

        @Test
        @DisplayName("성공: IllegalArgumentException을 HTTP 400으로 처리한다")
        void success_handleIllegalArgumentException() {
            // given
            IllegalArgumentException illegalArgException = new IllegalArgumentException("잘못된 인자");

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(illegalArgException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST.getCode()),
                        () -> assertThat(response.getBody().getMessage()).isEqualTo("잘못된 인자")
                );
                
                verify(mockLogger).logError("IllegalArgumentException", "잘못된 인자가 전달되었습니다.", illegalArgException);
            }
        }
    }

    @Nested
    @DisplayName("데이터베이스 예외 처리 테스트")
    class DatabaseExceptionHandlingTest {

    }

    @Nested
    @DisplayName("기타 예외 처리 테스트")
    class OtherExceptionHandlingTest {

        @Test
        @DisplayName("성공: NullPointerException을 HTTP 500으로 처리한다")
        void success_handleNullPointerException() {
            // given
            NullPointerException nullPointerException = new NullPointerException("null 값 참조");

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleNullPointerException(nullPointerException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.INTERNAL_SERVER_ERROR.getCode())
                );
                
                verify(mockLogger).logError("NullPointerException", "요청을 처리하는 중에 Null 값이 참조되었습니다.", nullPointerException);
            }
        }

        @Test
        @DisplayName("성공: NumberFormatException을 HTTP 400으로 처리한다")
        void success_handleNumberFormatException() {
            // given
            NumberFormatException numberFormatException = new NumberFormatException("숫자 형식 오류");

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleNumberFormatException(numberFormatException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST.getCode())
                );
                
                verify(mockLogger).logError("NumberFormatException", "숫자 형식이 잘못되었습니다.", numberFormatException);
            }
        }

        @Test
        @DisplayName("성공: IndexOutOfBoundsException을 HTTP 400으로 처리한다")
        void success_handleIndexOutOfBoundsException() {
            // given
            IndexOutOfBoundsException indexOutOfBoundsException = new IndexOutOfBoundsException("인덱스 범위 초과");

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleIndexOutOfBoundsException(indexOutOfBoundsException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST.getCode())
                );
                
                verify(mockLogger).logError("IndexOutOfBoundsException", "인덱스가 범위를 벗어났습니다.", indexOutOfBoundsException);
            }
        }
    }



    @Nested
    @DisplayName("기본 예외 처리 테스트")
    class GeneralExceptionHandlingTest {

        @Test
        @DisplayName("성공: 일반 Exception을 HTTP 500으로 처리한다")
        void success_handleGeneralException() {
            // given
            Exception generalException = new Exception("예상치 못한 오류");

            try (MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> mockedLoggerFactory = 
                 mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class)) {
                
                var mockLogger = mock(com.dataracy.modules.common.logging.CommonLogger.class);
                mockedLoggerFactory.when(com.dataracy.modules.common.logging.support.LoggerFactory::common)
                        .thenReturn(mockLogger);

                // when
                ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(generalException);

                // then
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().getCode()).isEqualTo(CommonErrorStatus.INTERNAL_SERVER_ERROR.getCode()),
                        () -> assertThat(response.getBody().getMessage()).isEqualTo("예상치 못한 오류")
                );
                
                verify(mockLogger).logError("Exception", "내부 서버 오류입니다.", generalException);
            }
        }

    }
}