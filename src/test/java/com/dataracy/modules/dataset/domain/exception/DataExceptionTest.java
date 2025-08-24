package com.dataracy.modules.dataset.domain.exception;

import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataExceptionTest {

    @Test
    void shouldHoldErrorCode() {
        DataException exception = new DataException(DataErrorStatus.NOT_FOUND_DATA);
        assertThat(exception.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }
}
