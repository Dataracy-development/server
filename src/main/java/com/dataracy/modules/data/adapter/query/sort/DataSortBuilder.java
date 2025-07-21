package com.dataracy.modules.data.adapter.query.sort;

import com.dataracy.modules.data.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.data.domain.enums.DataSortType;
import com.querydsl.core.types.OrderSpecifier;

public final class DataSortBuilder {
    /**
 * 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private DataSortBuilder() {}

    public static OrderSpecifier<?>[] fromSortOption(DataSortType sort) {
        QDataEntity data = QDataEntity.dataEntity;
        if (sort == null) {
            return new OrderSpecifier[]{data.createdAt.desc()};
        }

        return switch (sort) {
            case LATEST -> new OrderSpecifier[]{data.createdAt.desc()};
            case OLDEST -> new OrderSpecifier[]{data.createdAt.asc()};
            case DOWNLOAD -> new OrderSpecifier[]{data.downloadCount.desc()};
        };
    }
}
