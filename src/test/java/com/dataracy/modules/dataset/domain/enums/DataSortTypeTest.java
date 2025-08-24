package com.dataracy.modules.dataset.domain.enums;

import com.dataracy.modules.dataset.domain.exception.DataException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class DataSortTypeTest {

    @Test
    void ofShouldReturnEnumWhenValidInput() {
        assertThat(DataSortType.of("LATEST")).isEqualTo(DataSortType.LATEST);
        assertThat(DataSortType.of("oldest")).isEqualTo(DataSortType.OLDEST);
        assertThat(DataSortType.of("Download")).isEqualTo(DataSortType.DOWNLOAD);
        assertThat(DataSortType.of("utilize")).isEqualTo(DataSortType.UTILIZE);
    }

    @Test
    void ofShouldThrowDataExceptionWhenInvalidInput() {
        DataException exception = catchThrowableOfType(
                () -> DataSortType.of("invalid"),
                DataException.class
        );
        assertThat(exception).isNotNull();
    }
}
