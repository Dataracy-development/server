package com.dataracy.modules.dataset.application.service.validate;

import com.dataracy.modules.dataset.application.port.out.validate.CheckDataExistsByIdPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataValidateServiceTest {

    @Mock
    private CheckDataExistsByIdPort checkDataExistsByIdPort;

    private DataValidateService dataValidateService;

    @BeforeEach
    void setUp() {
        dataValidateService = new DataValidateService(checkDataExistsByIdPort);
    }

    @Nested
    @DisplayName("validateData 메서드 테스트")
    class ValidateDataTest {

        @Test
        @DisplayName("성공: 데이터가 존재할 때 검증 통과")
        void validateData_데이터존재_검증통과() {
            // given
            Long dataId = 1L;
            given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(true);

            // when & then
            dataValidateService.validateData(dataId);
            
            then(checkDataExistsByIdPort).should().existsDataById(dataId);
        }

        @Test
        @DisplayName("실패: 데이터가 존재하지 않을 때 DataException 발생")
        void validateData_데이터존재하지않음_DataException발생() {
            // given
            Long dataId = 999L;
            given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> dataValidateService.validateData(dataId))
                .isInstanceOf(DataException.class);
            
            then(checkDataExistsByIdPort).should().existsDataById(dataId);
        }
    }
}
