package com.dataracy.modules.dataset.domain.exception;

import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataExceptionTest {

    @Test
    @DisplayName("DataException 생성 시 전달한 ErrorStatus를 보관한다")
    void shouldHoldErrorCode() {
        // when
        DataException exception = new DataException(DataErrorStatus.NOT_FOUND_DATA);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }
}
