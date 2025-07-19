package com.dataracy.modules.project.adapter.query.sort;

import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.querydsl.core.types.OrderSpecifier;

public final class ProjectPopularOrderBuilder {
    private ProjectPopularOrderBuilder() {}

    public static OrderSpecifier<?> popularOrder() {
        QProjectEntity p = QProjectEntity.projectEntity;

        return p.likeCount.multiply(2)
                .add(p.commentCount.multiply(1.5))
                .add(p.viewCount)
                .desc();
    }
}
