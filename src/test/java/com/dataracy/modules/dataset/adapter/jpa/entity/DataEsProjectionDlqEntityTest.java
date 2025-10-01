package com.dataracy.modules.dataset.adapter.jpa.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DataEsProjectionDlqEntityTest {

    @Test
    @DisplayName("Builder 기본값 확인: deltaDownload=0, setDeleted=false")
    void builderShouldApplyDefaultValues() {
        // when
        DataEsProjectionDlqEntity entity = DataEsProjectionDlqEntity.builder()
                .dataId(100L)
                .lastError("error message")
                .build();

        // then
        assertAll(
                () -> assertThat(entity.getDeltaDownload()).isZero(),
                () -> assertThat(entity.getSetDeleted()).isFalse(),
                () -> assertThat(entity.getDataId()).isEqualTo(100L),
                () -> assertThat(entity.getLastError()).isEqualTo("error message")
        );
    }

    @Test
    @DisplayName("Setter로 필드 수정 가능해야 한다")
    void setterShouldUpdateFields() {
        // given
        DataEsProjectionDlqEntity entity = new DataEsProjectionDlqEntity();
        entity.setDataId(1L);
        entity.setDeltaDownload(5);
        entity.setSetDeleted(true);
        entity.setLastError("fail reason");

        // then
        assertAll(
                () -> assertThat(entity.getDataId()).isEqualTo(1L),
                () -> assertThat(entity.getDeltaDownload()).isEqualTo(5),
                () -> assertThat(entity.getSetDeleted()).isTrue(),
                () -> assertThat(entity.getLastError()).isEqualTo("fail reason")
        );
    }

    @Test
    @DisplayName("AllArgsConstructor 로 모든 값이 잘 세팅된다")
    void allArgsConstructorShouldSetAllFields() {
        // when
        DataEsProjectionDlqEntity entity = new DataEsProjectionDlqEntity(
                10L,
                200L,
                3,
                true,
                "dlq error"
        );

        // then
        assertAll(
                () -> assertThat(entity.getId()).isEqualTo(10L),
                () -> assertThat(entity.getDataId()).isEqualTo(200L),
                () -> assertThat(entity.getDeltaDownload()).isEqualTo(3),
                () -> assertThat(entity.getSetDeleted()).isTrue(),
                () -> assertThat(entity.getLastError()).isEqualTo("dlq error")
        );
    }
}
