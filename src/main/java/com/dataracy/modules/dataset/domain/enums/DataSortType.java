package com.dataracy.modules.dataset.domain.enums;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DataSortType {
    LATEST("LATEST"),
    OLDEST("OLDEST"),
    DOWNLOAD("DOWNLOAD"),
    UTILIZE("UTILIZE")
    ;

    private final String value;

    /**
     * 입력된 문자열에 해당하는 DataSortType 열거형 상수를 반환합니다.
     *
     * 입력값은 열거형 이름 또는 value 필드와 대소문자 구분 없이 비교됩니다.
     * 일치하는 값이 없으면 로그를 남기고 DataException을 발생시킵니다.
     *
     * @param input DataSortType을 식별할 문자열
     * @return 입력값에 해당하는 DataSortType 상수
     * @throws DataException 유효하지 않은 정렬 타입 입력 시 발생
     */
    public static DataSortType of(String input) {
        return Arrays.stream(DataSortType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("DataSortType", "잘못된 ENUM 타입입니다. LATEST, OLDEST, DOWNLOAD, UTILIZE만 가능합니다.");
                    return new DataException(DataErrorStatus.INVALID_DATA_SORT_TYPE);
                });
    }
}
