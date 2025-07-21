package com.dataracy.modules.dataset.domain.enums;

import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 데이터셋 정렬 enum
 */
@Getter
@RequiredArgsConstructor
public enum DataSortType {
    LATEST("LATEST"),
    OLDEST("OLDEST"),
    DOWNLOAD("DOWNLOAD"),
    ;

    private final String value;

    public static DataSortType of(String input) {

        return Arrays.stream(DataSortType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new DataException(DataErrorStatus.INVALID_DATA_SORT_TYPE));
    }
}
