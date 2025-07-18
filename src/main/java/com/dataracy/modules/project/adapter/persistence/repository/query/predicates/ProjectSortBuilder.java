package com.dataracy.modules.project.adapter.persistence.repository.query.predicates;

import com.querydsl.core.types.OrderSpecifier;

import static com.dataracy.modules.project.adapter.persistence.entity.QProjectEntity.projectEntity;

public final class ProjectSortBuilder {
    public static OrderSpecifier<?>[] fromSortOption(String sort) {
        String lowerCase = sort.toLowerCase();
        return switch (lowerCase) {
            case "created_desc" -> new OrderSpecifier[]{projectEntity.createdAt.desc()};
            case "created_asc" -> new OrderSpecifier[]{projectEntity.createdAt.asc()};
//            case "LIKE_DESC" -> new OrderSpecifier[]{projectEntity.likeCount.desc()};
//            case "REVIEW_DESC" -> new OrderSpecifier[]{projectEntity.reviewCount.desc()};
//            case "VIEWS_DESC" -> new OrderSpecifier[]{projectEntity.viewCount.desc()};
            default -> new OrderSpecifier[]{projectEntity.createdAt.desc()};
        };
    }
}
