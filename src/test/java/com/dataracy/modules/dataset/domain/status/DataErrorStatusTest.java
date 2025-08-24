package com.dataracy.modules.dataset.domain.status;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataErrorStatusTest {

    @Test
    void shouldContainErrorCodes() {
        assertThat(DataErrorStatus.FAIL_UPLOAD_DATA.getCode()).isEqualTo("DATA-001");
        assertThat(DataErrorStatus.NOT_FOUND_DATA.getHttpStatus().value()).isEqualTo(404);
    }
}
