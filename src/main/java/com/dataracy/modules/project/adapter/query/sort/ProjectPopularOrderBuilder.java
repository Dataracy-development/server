package com.dataracy.modules.project.adapter.query.sort;

import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.querydsl.core.types.OrderSpecifier;

public final class ProjectPopularOrderBuilder {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectPopularOrderBuilder() {}

    /**
     * 프로젝트의 인기 점수를 계산하여 내림차순으로 정렬하는 OrderSpecifier를 반환합니다.
     *
     * 인기 점수는 (좋아요 수 × 2) + (댓글 수 × 1.5) + 조회수로 산출됩니다.
     *
     * @return 프로젝트 인기 점수 기준 내림차순 정렬 OrderSpecifier
     */
    public static OrderSpecifier<?> popularOrder() {
        QProjectEntity project = QProjectEntity.projectEntity;

        return project.likeCount.multiply(2)
                .add(project.commentCount.multiply(1.5))
                .add(project.viewCount)
                .desc();
    }
}
