package com.dataracy.modules.project.adapter.query.sort;

import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.querydsl.core.types.OrderSpecifier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ProjectSortBuilder {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectSortBuilder() {}

    public static OrderSpecifier<?>[] fromSortOption(ProjectSortType sort) {
        QProjectEntity project = QProjectEntity.projectEntity;
        if (sort == null) {
            return new OrderSpecifier[]{project.createdAt.desc()};
        }

        return switch (sort) {
            case LATEST -> new OrderSpecifier[]{project.createdAt.desc()};
            case OLDEST -> new OrderSpecifier[]{project.createdAt.asc()};
            case MOST_LIKED -> new OrderSpecifier[]{project.likeCount.desc()};
            case MOST_VIEWED -> new OrderSpecifier[]{project.viewCount.desc()};
            case MOST_COMMENTED -> new OrderSpecifier[]{project.commentCount.desc()};
            case LEAST_COMMENTED -> new OrderSpecifier[]{project.commentCount.asc()};
        };
    }
}
