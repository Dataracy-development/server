package com.dataracy.modules.common.exception;

import com.dataracy.modules.common.dto.response.ErrorResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.logging.CommonLogger;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.status.CommonSuccessStatus;
import com.dataracy.modules.common.support.lock.LockAcquisitionException;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.security.exception.SecurityException;
import com.dataracy.modules.security.status.SecurityErrorStatus;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.core.MethodParameter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        // LoggerFactory 모킹을 각 테스트에서 개별적으로 설정
    }

    @Nested
    @DisplayName("BusinessException 처리 테스트")
    class BusinessExceptionTest {

        @Test
        @DisplayName("BusinessException 처리 - DataException")
        void handleBusinessException_DataException() {
            // given
            DataException exception = new DataException(DataErrorStatus.NOT_FOUND_DATA);

            // when
            ResponseEntity<ErrorResponse> response = handler.handleCustomException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("DATA-002");
            assertThat(response.getBody().getMessage()).isEqualTo("해당 데이터셋 리소스가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("BusinessException 처리 - ProjectException")
        void handleBusinessException_ProjectException() {
            // given
            ProjectException exception = new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);

            // when
            ResponseEntity<ErrorResponse> response = handler.handleCustomException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("PROJECT-002");
            assertThat(response.getBody().getMessage()).isEqualTo("해당 프로젝트 리소스가 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("CommonException 처리 테스트")
    class CommonExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("CommonException 처리")
        void handleCommonException() {
            // given
            CommonException exception = new CommonException(CommonErrorStatus.INTERNAL_SERVER_ERROR);

            // when
            ResponseEntity<ErrorResponse> response = handler.handleCommonException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-500");
            assertThat(response.getBody().getMessage()).isEqualTo("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.");
        }
    }

    @Nested
    @DisplayName("LockAcquisitionException 처리 테스트")
    class LockAcquisitionExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("LockAcquisitionException 처리")
        void handleLockError() {
            // given
            LockAcquisitionException exception = new LockAcquisitionException("Lock acquisition failed");

            // when
            ResponseEntity<ErrorResponse> response = handler.handleLockError(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-409");
            assertThat(response.getBody().getMessage()).isEqualTo("Lock acquisition failed");
        }
    }

    @Nested
    @DisplayName("SecurityException 처리 테스트")
    class SecurityExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("SecurityException 처리")
        void handleSecurityException() {
            // given
            SecurityException exception = new SecurityException(SecurityErrorStatus.UNEXPECTED_PRINCIPAL_TYPE_NOT_USER_DETAILS);

            // when
            ResponseEntity<ErrorResponse> response = handler.handleSecurityException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-401");
            assertThat(response.getBody().getMessage()).isEqualTo("인증 객체에 저장된 인증 객체의 principal가 CustomUserDetails가 아닙니다.");
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException 처리 테스트")
    class IllegalArgumentExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("IllegalArgumentException 처리")
        void handleIllegalArgumentException() {
            // given
            IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

            // when
            ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-400");
            assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument");
        }
    }

    @Nested
    @DisplayName("NullPointerException 처리 테스트")
    class NullPointerExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("NullPointerException 처리")
        void handleNullPointerException() {
            // given
            NullPointerException exception = new NullPointerException("Null pointer");

            // when
            ResponseEntity<ErrorResponse> response = handler.handleNullPointerException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-500");
            assertThat(response.getBody().getMessage()).isEqualTo("Null pointer");
        }
    }

    @Nested
    @DisplayName("NumberFormatException 처리 테스트")
    class NumberFormatExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("NumberFormatException 처리")
        void handleNumberFormatException() {
            // given
            NumberFormatException exception = new NumberFormatException("Invalid number format");

            // when
            ResponseEntity<ErrorResponse> response = handler.handleNumberFormatException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-400");
            assertThat(response.getBody().getMessage()).isEqualTo("Invalid number format");
        }
    }

    @Nested
    @DisplayName("IndexOutOfBoundsException 처리 테스트")
    class IndexOutOfBoundsExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("IndexOutOfBoundsException 처리")
        void handleIndexOutOfBoundsException() {
            // given
            IndexOutOfBoundsException exception = new IndexOutOfBoundsException("Index out of bounds");

            // when
            ResponseEntity<ErrorResponse> response = handler.handleIndexOutOfBoundsException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-400");
            assertThat(response.getBody().getMessage()).isEqualTo("Index out of bounds");
        }
    }

    @Nested
    @DisplayName("ConstraintViolationException 처리 테스트")
    class ConstraintViolationExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("ConstraintViolationException 처리")
        void handleValidationParameterException() {
            // given
            ConstraintViolationException exception = new ConstraintViolationException("Validation failed", null);

            // when
            ResponseEntity<ErrorResponse> response = handler.handleValidationParameterException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-400");
            assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        }
    }

    @Nested
    @DisplayName("MissingRequestHeaderException 처리 테스트")
    class MissingRequestHeaderExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("MissingRequestHeaderException 처리")
        void handleMissingRequestHeaderException() {
            // given
            MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);
            when(exception.getHeaderName()).thenReturn("Authorization");
            when(exception.getMessage()).thenReturn("Required request header 'Authorization' for method parameter type String is not present");

            // when
            ResponseEntity<ErrorResponse> response = handler.handleMissingRequestHeaderException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-400");
            assertThat(response.getBody().getMessage()).contains("Authorization");
        }
    }

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException 처리 테스트")
    class MethodArgumentTypeMismatchExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("MethodArgumentTypeMismatchException 처리")
        void handleMethodArgumentTypeMismatchException() {
            // given
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Invalid type");
            MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                    "invalid", String.class, "id", null, illegalArgumentException);

            // when
            ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(illegalArgumentException);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-400");
            assertThat(response.getBody().getMessage()).contains("Invalid type");
        }
    }

    @Nested
    @DisplayName("Exception 처리 테스트")
    class ExceptionTest {

        private MockedStatic<LoggerFactory> loggerFactoryMock;

        @BeforeEach
        void setUp() {
            loggerFactoryMock = mockStatic(LoggerFactory.class);
            // CommonLogger 모킹
            CommonLogger commonLogger = mock(CommonLogger.class);
            loggerFactoryMock.when(LoggerFactory::common).thenReturn(commonLogger);
            lenient().doNothing().when(commonLogger).logError(anyString(), anyString(), any(Exception.class));
        }

        @AfterEach
        void tearDown() {
            if (loggerFactoryMock != null) {
                loggerFactoryMock.close();
            }
        }

        @Test
        @DisplayName("일반 Exception 처리")
        void handleException() {
            // given
            Exception exception = new Exception("General exception");

            // when
            ResponseEntity<ErrorResponse> response = handler.handleException(exception);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("COMMON-500");
            assertThat(response.getBody().getMessage()).isEqualTo("General exception");
        }
    }
}
