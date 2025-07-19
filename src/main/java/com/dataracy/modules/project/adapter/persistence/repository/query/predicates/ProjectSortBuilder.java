package com.dataracy.modules.project.adapter.persistence.repository.query.predicates;

import com.dataracy.modules.project.adapter.persistence.entity.QProjectEntity;
import com.querydsl.core.types.OrderSpecifier;

import static com.dataracy.modules.project.adapter.persistence.entity.QProjectEntity.projectEntity;

public final class ProjectSortBuilder {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectSortBuilder() {}

    private static final String CREATED_DESC = "created_desc";
    private static final String CREATED_ASC = "created_asc";

    /**
     * 주어진 정렬 옵션 문자열에 따라 프로젝트 엔티티의 정렬 기준 배열을 반환합니다.
     *
     * @param sort 정렬 옵션 문자열(예: "created_desc", "created_asc"). null 또는 빈 문자열인 경우 생성일 내림차순이 기본값입니다.
     * @return 정렬 기준에 해당하는 OrderSpecifier 배열
     */
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

    public static OrderSpecifier<?> popularOrder() {
        QProjectEntity p = QProjectEntity.projectEntity;

        return p.likeCount.multiply(2)
                .add(p.commentCount.multiply(1.5))
                .add(p.viewCount)
                .desc();
    }
}
