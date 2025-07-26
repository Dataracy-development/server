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

    @Override
    public Page<Comment> findReplyComments(Long projectId, Long commentId, Pageable pageable) {
        List<CommentEntity> entities = queryFactory
                .selectFrom(comment)
                .where(
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
                                CommentFilterPredicate.parentCommentIdEq(commentId)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }
}
