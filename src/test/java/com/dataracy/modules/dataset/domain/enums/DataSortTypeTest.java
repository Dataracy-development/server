package com.dataracy.modules.dataset.domain.enums;

import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class DataSortTypeTest {

    @ParameterizedTest(name = "of - {0} 문자열로 {1} enum을 반환한다")
    @CsvSource({
            "LATEST, LATEST",
            "latest, LATEST",
            "OLDEST, OLDEST",
            "oldest, OLDEST",
            "DOWNLOAD, DOWNLOAD",
            "download, DOWNLOAD",
            "UTILIZE, UTILIZE",
            "utilize, UTILIZE"
    })
    @DisplayName("of - 문자열로 해당 enum을 반환한다")
    void of_WhenValidString_ReturnsCorrespondingEnum(String input, String expectedEnumName) {
        // when
        DataSortType result = DataSortType.of(input);

        // then
        assertThat(result.name()).isEqualTo(expectedEnumName);
    }

    @ParameterizedTest(name = "of - {0}로 DataException이 발생한다")
    @CsvSource({
            "INVALID, 'INVALID'",
            "null, null",
            "'', ''"
    })
    @DisplayName("of - 잘못된 입력으로 DataException이 발생한다")
    void of_WhenInvalidInput_ThrowsDataException(String input, String expectedInput) {
        // when & then
        String actualInput = "null".equals(input) ? null : input;
        DataException exception = catchThrowableOfType(
                () -> DataSortType.of(actualInput),
                DataException.class
        );
        assertAll(
                () -> assertThat(exception).isNotNull(),
                () -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", DataErrorStatus.INVALID_DATA_SORT_TYPE)
        );
    }

    @Test
    @DisplayName("getValue - LATEST의 value를 반환한다")
    void getValue_WhenLatest_ReturnsLatestValue() {
        // when
        String result = DataSortType.LATEST.getValue();

        // then
        assertThat(result).isEqualTo("LATEST");
    }

    @Test
    @DisplayName("getValue - OLDEST의 value를 반환한다")
    void getValue_WhenOldest_ReturnsOldestValue() {
        // when
        String result = DataSortType.OLDEST.getValue();

        // then
        assertThat(result).isEqualTo("OLDEST");
    }

    @Test
    @DisplayName("getValue - DOWNLOAD의 value를 반환한다")
    void getValue_WhenDownload_ReturnsDownloadValue() {
        // when
        String result = DataSortType.DOWNLOAD.getValue();

        // then
        assertThat(result).isEqualTo("DOWNLOAD");
    }

    @Test
    @DisplayName("getValue - UTILIZE의 value를 반환한다")
    void getValue_WhenUtilize_ReturnsUtilizeValue() {
        // when
        String result = DataSortType.UTILIZE.getValue();

        // then
        assertThat(result).isEqualTo("UTILIZE");
    }
}