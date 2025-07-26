package com.dataracy.modules.comment.adapter.query;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.adapter.jpa.entity.QCommentEntity;
import com.dataracy.modules.comment.adapter.jpa.mapper.CommentEntityMapper;
import com.dataracy.modules.comment.adapter.query.predicates.CommentFilterPredicate;
import com.dataracy.modules.comment.adapter.query.sort.CommentSortBuilder;
import com.dataracy.modules.comment.application.dto.response.CommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.port.query.CommentQueryRepositoryPort;
import com.dataracy.modules.comment.domain.model.Comment;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryPortAdapter implements CommentQueryRepositoryPort {
    private final JPAQueryFactory queryFactory;

    private final QCommentEntity comment = QCommentEntity.commentEntity;

    /**
     * 주어진 ID에 해당하는 댓글을 조회하여 도메인 모델로 반환합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글이 존재하면 해당 댓글의 도메인 모델을, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        CommentEntity entity = queryFactory
                .selectFrom(comment)
                .where(
                        CommentFilterPredicate.commentIdEq(commentId)
                )
                .fetchOne();

        return Optional.ofNullable(CommentEntityMapper.toDomain(entity));
    }

    /**
     * 지정된 프로젝트의 루트 댓글 목록과 각 댓글의 답글 수를 페이지 단위로 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @param pageable 페이지네이션 및 정렬 정보
     * @return 각 댓글과 답글 수를 포함하는 CommentWithReplyCountResponse의 페이지 객체
     */
    @Override
    public Page<CommentWithReplyCountResponse> findComments(Long projectId, Pageable pageable) {
        QCommentEntity child = new QCommentEntity("child");

        List<Tuple> tuples = queryFactory
                .select(
                        comment,
                        JPAExpressions
                                .select(child.count())
                                .from(child)
                                .where(child.parentCommentId.eq(comment.id))
                )
                .from(comment)
                .where(
                        CommentFilterPredicate.projectIdEq(projectId),
                        CommentFilterPredicate.isRootComment()
                )
                .orderBy(CommentSortBuilder.createdAtDesc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<CommentWithReplyCountResponse> contents = tuples.stream()
                .map(tuple -> new CommentWithReplyCountResponse(
                        CommentEntityMapper.toDomain(tuple.get(comment)),
                        tuple.get(1, Long.class)
                ))
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(comment.count())
                        .from(comment)
                        .where(
                                CommentFilterPredicate.projectIdEq(projectId),
                                CommentFilterPredicate.isRootComment()
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 지정된 프로젝트와 부모 댓글 ID에 해당하는 답글(대댓글) 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @param commentId 부모 댓글의 ID
     * @param pageable 페이지네이션 및 정렬 정보
     * @return 답글 목록과 전체 개수를 포함하는 Page 객체
     */
    @Override
    public Page<Comment> findReplyComments(Long projectId, Long commentId, Pageable pageable) {
        List<CommentEntity> entities = queryFactory
                .selectFrom(comment)
                .where(
                        CommentFilterPredicate.projectIdEq(projectId),
                        CommentFilterPredicate.parentCommentIdEq(commentId)
                )
                .orderBy(CommentSortBuilder.createdAtDesc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Comment> contents = entities.stream()
                .map(CommentEntityMapper::toDomain
                )
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(comment.count())
                        .from(comment)
                        .where(
                                CommentFilterPredicate.projectIdEq(projectId),
                                CommentFilterPredicate.parentCommentIdEq(commentId)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }
}
