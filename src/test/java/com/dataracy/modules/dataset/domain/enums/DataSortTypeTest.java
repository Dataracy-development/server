package com.dataracy.modules.dataset.domain.enums;

import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSortTypeTest {

    @Test
    @DisplayName("of - LATEST 문자열로 LATEST enum을 반환한다")
    void of_WhenLatestString_ReturnsLatestEnum() {
        // when
        DataSortType result = DataSortType.of("LATEST");

        // then
        assertThat(result).isEqualTo(DataSortType.LATEST);
    }

    @Test
    @DisplayName("of - latest 소문자로 LATEST enum을 반환한다")
    void of_WhenLatestLowerCase_ReturnsLatestEnum() {
        // when
        DataSortType result = DataSortType.of("latest");

        // then
        assertThat(result).isEqualTo(DataSortType.LATEST);
    }

    @Test
    @DisplayName("of - OLDEST 문자열로 OLDEST enum을 반환한다")
    void of_WhenOldestString_ReturnsOldestEnum() {
        // when
        DataSortType result = DataSortType.of("OLDEST");

        // then
        assertThat(result).isEqualTo(DataSortType.OLDEST);
    }

    @Test
    @DisplayName("of - oldest 소문자로 OLDEST enum을 반환한다")
    void of_WhenOldestLowerCase_ReturnsOldestEnum() {
        // when
        DataSortType result = DataSortType.of("oldest");

        // then
        assertThat(result).isEqualTo(DataSortType.OLDEST);
    }

    @Test
    @DisplayName("of - DOWNLOAD 문자열로 DOWNLOAD enum을 반환한다")
    void of_WhenDownloadString_ReturnsDownloadEnum() {
        // when
        DataSortType result = DataSortType.of("DOWNLOAD");

        // then
        assertThat(result).isEqualTo(DataSortType.DOWNLOAD);
    }

    @Test
    @DisplayName("of - download 소문자로 DOWNLOAD enum을 반환한다")
    void of_WhenDownloadLowerCase_ReturnsDownloadEnum() {
        // when
        DataSortType result = DataSortType.of("download");

        // then
        assertThat(result).isEqualTo(DataSortType.DOWNLOAD);
    }

    @Test
    @DisplayName("of - UTILIZE 문자열로 UTILIZE enum을 반환한다")
    void of_WhenUtilizeString_ReturnsUtilizeEnum() {
        // when
        DataSortType result = DataSortType.of("UTILIZE");

        // then
        assertThat(result).isEqualTo(DataSortType.UTILIZE);
    }

    @Test
    @DisplayName("of - utilize 소문자로 UTILIZE enum을 반환한다")
    void of_WhenUtilizeLowerCase_ReturnsUtilizeEnum() {
        // when
        DataSortType result = DataSortType.of("utilize");

        // then
        assertThat(result).isEqualTo(DataSortType.UTILIZE);
    }

    @Test
    @DisplayName("of - 유효하지 않은 문자열로 DataException이 발생한다")
    void of_WhenInvalidString_ThrowsDataException() {
        // when & then
        assertThatThrownBy(() -> DataSortType.of("INVALID"))
                .isInstanceOf(DataException.class)
                .hasFieldOrPropertyWithValue("errorCode", DataErrorStatus.INVALID_DATA_SORT_TYPE);
    }

    @Test
    @DisplayName("of - null로 DataException이 발생한다")
    void of_WhenNull_ThrowsDataException() {
        // when & then
        assertThatThrownBy(() -> DataSortType.of(null))
                .isInstanceOf(DataException.class)
                .hasFieldOrPropertyWithValue("errorCode", DataErrorStatus.INVALID_DATA_SORT_TYPE);
    }

    @Test
    @DisplayName("of - 빈 문자열로 DataException이 발생한다")
    void of_WhenEmptyString_ThrowsDataException() {
        // when & then
        assertThatThrownBy(() -> DataSortType.of(""))
                .isInstanceOf(DataException.class)
                .hasFieldOrPropertyWithValue("errorCode", DataErrorStatus.INVALID_DATA_SORT_TYPE);
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