package com.dataracy.modules.dataset.domain.model.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataUploadEventTest {

    @Test
    void shouldCreateEventWithAllFields() {
        DataUploadEvent event = new DataUploadEvent(1L, "url", "file.csv");

        assertThat(event.getDataId()).isEqualTo(1L);
        assertThat(event.getDataFileUrl()).isEqualTo("url");
        assertThat(event.getOriginalFilename()).isEqualTo("file.csv");
    }
}
