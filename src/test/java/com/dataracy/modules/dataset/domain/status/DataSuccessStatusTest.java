package com.dataracy.modules.dataset.domain.status;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataSuccessStatusTest {

    @Test
    void shouldContainSuccessCodes() {
        assertThat(DataSuccessStatus.CREATED_DATASET.getCode()).isEqualTo("201");
        assertThat(DataSuccessStatus.GET_DATA_DETAIL.getHttpStatus().is2xxSuccessful()).isTrue();
    }
}
