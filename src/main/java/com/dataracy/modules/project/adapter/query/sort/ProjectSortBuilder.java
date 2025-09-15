package com.dataracy.modules.project.adapter.query.sort;

import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.querydsl.core.types.OrderSpecifier;

public final class ProjectSortBuilder {
    private ProjectSortBuilder() {}

    /**
     * 프로젝트 정렬 옵션에 따라 QueryDSL 정렬 조건 배열을 반환합니다.
     *
     * @param sort 적용할 프로젝트 정렬 타입. null인 경우 최신순(생성일 내림차순)으로 정렬됩니다.
     * @return 정렬 조건이 담긴 OrderSpecifier 배열
     */
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
