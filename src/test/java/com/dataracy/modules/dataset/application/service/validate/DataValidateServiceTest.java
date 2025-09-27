package com.dataracy.modules.dataset.application.service.validate;

import com.dataracy.modules.dataset.application.port.out.validate.CheckDataExistsByIdPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataValidateServiceTest {

    @InjectMocks
    private DataValidateService service;

    @Mock
    private CheckDataExistsByIdPort port;

    private MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> loggerFactoryMock;
    private com.dataracy.modules.common.logging.ServiceLogger loggerService;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class);
        loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
        loggerFactoryMock.when(() -> com.dataracy.modules.common.logging.support.LoggerFactory.service()).thenReturn(loggerService);
        lenient().when(loggerService.logStart(anyString(), anyString())).thenReturn(Instant.now());
        lenient().doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
        lenient().doNothing().when(loggerService).logWarning(anyString(), anyString());
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("데이터셋 존재 검증")
    class ExistsDataSets {

        @Test
        @DisplayName("데이터셋 존재 → 검증 통과 및 로깅 검증")
        void validateDataShouldPassWhenExists() {
            // given
            Long dataId = 1L;
            given(port.existsDataById(dataId)).willReturn(true);

            // when
            service.validateData(dataId);

            // then
            then(port).should().existsDataById(dataId);
            
            // 로깅 검증
            then(loggerService).should().logStart(eq("ValidateDataUseCase"), 
                contains("주어진 데이터 ID에 해당하는 데이터의 존재 여부를 검증 서비스 시작"));
            then(loggerService).should().logSuccess(eq("ValidateDataUseCase"), 
                contains("주어진 데이터 ID에 해당하는 데이터의 존재 여부를 검증 서비스 종료"), any(Instant.class));
            then(loggerService).should(never()).logWarning(anyString(), anyString());
        }

        @Test
        @DisplayName("데이터셋 미존재 → DataException(NOT_FOUND_DATA) 발생 및 로깅 검증")
        void validateDataShouldThrowWhenNotExists() {
            // given
            Long dataId = 1L;
            given(port.existsDataById(dataId)).willReturn(false);

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> service.validateData(dataId),
                    DataException.class
            );
            
            // 예외 검증
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
            
            // 포트 호출 검증
            then(port).should().existsDataById(dataId);
            
            // 로깅 검증
            then(loggerService).should().logStart(eq("ValidateDataUseCase"), 
                contains("주어진 데이터 ID에 해당하는 데이터의 존재 여부를 검증 서비스 시작"));
            then(loggerService).should().logWarning(eq("ValidateDataUseCase"), 
                contains("해당 데이터셋이 존재하지 않습니다"));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }

        @Test
        @DisplayName("null 데이터 ID → IllegalArgumentException 발생")
        void validateDataShouldThrowWhenDataIdIsNull() {
            // given
            Long dataId = null;
            given(port.existsDataById(dataId)).willThrow(new IllegalArgumentException("Data ID cannot be null"));

            // when & then
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> service.validateData(dataId),
                    IllegalArgumentException.class
            );
            
            assertThat(ex.getMessage()).contains("Data ID cannot be null");
            
            // 로깅 검증
            then(loggerService).should().logStart(eq("ValidateDataUseCase"), 
                contains("주어진 데이터 ID에 해당하는 데이터의 존재 여부를 검증 서비스 시작"));
        }
    }
}
