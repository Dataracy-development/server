package com.dataracy.modules.dataset.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 데이터셋 정렬 enum
 */
@Getter
@RequiredArgsConstructor
public enum DataSortType {
    LATEST,
    OLDEST,
    DOWNLOAD,
    UTILIZE
    ;
}
