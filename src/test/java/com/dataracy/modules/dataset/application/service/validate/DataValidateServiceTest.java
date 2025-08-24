package com.dataracy.modules.dataset.application.service.validate;

import com.dataracy.modules.dataset.application.port.out.validate.CheckDataExistsByIdPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataValidateServiceTest {

    @InjectMocks
    private DataValidateService service;

    @Mock private CheckDataExistsByIdPort port;

    @Test
    void validateDataShouldPassWhenExists() {
        given(port.existsDataById(1L)).willReturn(true);
        service.validateData(1L); // no exception
    }

    @Test
    void validateDataShouldThrowWhenNotExists() {
        given(port.existsDataById(1L)).willReturn(false);
        DataException ex = catchThrowableOfType(() -> service.validateData(1L), DataException.class);
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }
}
