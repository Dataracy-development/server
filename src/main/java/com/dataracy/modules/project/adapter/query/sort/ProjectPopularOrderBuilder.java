package com.dataracy.modules.project.adapter.query.sort;

import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.querydsl.core.types.OrderSpecifier;

public final class ProjectPopularOrderBuilder {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectPopularOrderBuilder() {}

    /**
     * 프로젝트의 인기 점수를 기반으로 내림차순 정렬하는 OrderSpecifier를 반환합니다.
     *
     * 인기 점수는 좋아요 수에 2를 곱하고, 댓글 수에 1.5를 곱한 뒤, 조회수를 더해 계산됩니다.
     *
     * @return 인기 점수를 기준으로 내림차순 정렬하는 OrderSpecifier 객체
     */
    public static OrderSpecifier<?> popularOrder() {
        QProjectEntity project = QProjectEntity.projectEntity;

        return project.likeCount.multiply(2)
                .add(project.commentCount.multiply(1.5))
                .add(project.viewCount)
                .desc();
    }
}
