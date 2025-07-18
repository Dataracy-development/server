package com.dataracy.modules.project.adapter.persistence.repository.query.predicates;

import com.querydsl.core.types.OrderSpecifier;

import static com.dataracy.modules.project.adapter.persistence.entity.QProjectEntity.projectEntity;

public final class ProjectSortBuilder {
    private ProjectSortBuilder() {}

    private static final String CREATED_DESC = "created_desc";
    private static final String CREATED_ASC = "created_asc";

    public static OrderSpecifier<?>[] fromSortOption(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return new OrderSpecifier[]{projectEntity.createdAt.desc()};
        }

        String lowerCase = sort.toLowerCase();
        return switch (lowerCase) {
            case CREATED_DESC -> new OrderSpecifier[]{projectEntity.createdAt.desc()};
            case CREATED_ASC -> new OrderSpecifier[]{projectEntity.createdAt.asc()};
//          case "LIKE_DESC" -> new OrderSpecifier[]{projectEntity.likeCount.desc()};
//          case "REVIEW_DESC" -> new OrderSpecifier[]{projectEntity.reviewCount.desc()};
//          case "VIEWS_DESC" -> new OrderSpecifier[]{projectEntity.viewCount.desc()};
            default -> new OrderSpecifier[]{projectEntity.createdAt.desc()};
        };
    }
}
