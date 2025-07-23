package com.dataracy.modules.dataset.adapter.query.sort;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;

public final class DataSortBuilder {
    /**
 * DataSortBuilder 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private DataSortBuilder() {}

    /**
     * 주어진 정렬 옵션과 프로젝트 수 경로에 따라 데이터 쿼리의 정렬 기준 배열을 반환합니다.
     *
     * @param sort 데이터 정렬 기준을 지정하는 열거형 값입니다. null이면 생성일 기준 내림차순으로 정렬합니다.
     * @param projectCountPath 프로젝트 수를 나타내는 NumberPath로, UTILIZE 정렬 옵션에서 사용됩니다.
     * @return 지정된 정렬 옵션에 따른 OrderSpecifier 배열
     */
    public static OrderSpecifier<?>[] fromSortOption(DataSortType sort, NumberPath<Long> projectCountPath) {
        QDataEntity data = QDataEntity.dataEntity;
        if (sort == null) {
            return new OrderSpecifier[]{data.createdAt.desc()};
        }

        return switch (sort) {
            case LATEST -> new OrderSpecifier[]{data.createdAt.desc()};
            case OLDEST -> new OrderSpecifier[]{data.createdAt.asc()};
            case DOWNLOAD -> new OrderSpecifier[]{data.downloadCount.desc()};
            case UTILIZE -> new OrderSpecifier[]{projectCountPath.desc()};
        };
    }
}
