package com.dataracy.modules.dataset.application.service.validate;

import com.dataracy.modules.dataset.application.port.out.validate.CheckDataExistsByIdPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DataValidateServiceTest {

    @InjectMocks
    private DataValidateService service;

    @Mock
    private CheckDataExistsByIdPort port;

    @Nested
    @DisplayName("데이터셋 존재 검증")
    class ExistsDataSets {

        @Test
        @DisplayName("데이터셋 존재 → 검증 통과")
        void validateDataShouldPassWhenExists() {
            // given
            given(port.existsDataById(1L)).willReturn(true);

            // when (예외 없음)
            service.validateData(1L);

            // then (별도 예외 발생 없음 확인)
        }

        @Test
        @DisplayName("데이터셋 미존재 → DataException(NOT_FOUND_DATA) 발생")
        void validateDataShouldThrowWhenNotExists() {
            // given
            given(port.existsDataById(1L))
                    .willReturn(false);

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> service.validateData(1L),
                    DataException.class
            );
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
        }
    }
}
