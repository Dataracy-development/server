package com.dataracy.modules.dataset.domain.enums;

import com.dataracy.modules.dataset.domain.exception.DataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class DataSortTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"LATEST", "OLDEST", "DOWNLOAD", "UTILIZE"})
    @DisplayName("유효한 문자열이면 올바른 Enum을 반환한다")
    void ofShouldReturnEnumWhenValidInput(String input) {
        // when
        DataSortType result = DataSortType.of(input);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("잘못된 문자열이면 DataException을 던진다")
    void ofShouldThrowDataExceptionWhenInvalidInput() {
        // when
        DataException exception = catchThrowableOfType(
                () -> DataSortType.of("invalid"),
                DataException.class
        );

        // then
        assertThat(exception).isNotNull();
    }
}
