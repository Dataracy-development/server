package com.dataracy.modules.dataset.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DataStatusTest {

  @Test
  @DisplayName("DataErrorStatus 값 검증")
  void shouldContainErrorCodes() {
    assertAll(
        () -> assertThat(DataErrorStatus.FAIL_UPLOAD_DATA.getCode()).isEqualTo("DATA-001"),
        () -> assertThat(DataErrorStatus.NOT_FOUND_DATA.getHttpStatus().value()).isEqualTo(404));
  }

  @Test
  @DisplayName("DataErrorStatus 값 검증")
  void shouldContainSuccessCodes() {
    assertAll(
        () -> assertThat(DataSuccessStatus.CREATED_DATASET.getCode()).isEqualTo("201"),
        () ->
            assertThat(DataSuccessStatus.GET_DATA_DETAIL.getHttpStatus().is2xxSuccessful())
                .isTrue());
  }
}
