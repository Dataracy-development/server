package com.dataracy.modules.comment.adapter.query;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.adapter.jpa.entity.QCommentEntity;
import com.dataracy.modules.comment.adapter.jpa.mapper.CommentEntityMapper;
import com.dataracy.modules.comment.adapter.query.predicates.CommentFilterPredicate;
import com.dataracy.modules.comment.adapter.query.sort.CommentSortBuilder;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.port.out.query.read.ReadCommentPort;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReadCommentPortAdapter implements ReadCommentPort {
    private final JPAQueryFactory queryFactory;

    // Entity 상수 정의
    private static final String COMMENT_ENTITY = "CommentEntity";

    private final QCommentEntity comment = QCommentEntity.commentEntity;

    /**
     * 주어진 ID로 댓글을 조회하여 도메인 모델로 반환합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글이 존재하면 해당 댓글의 도메인 모델을 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        Instant startTime = LoggerFactory.query().logQueryStart(COMMENT_ENTITY, "[findCommentById] 주어진 ID에 해당하는 댓글 조회 시작. commentId=" + commentId);

        CommentEntity entity = queryFactory
                .selectFrom(comment)
                .where(
                        CommentFilterPredicate.commentIdEq(commentId)
                )
                .fetchOne();

        Optional<Comment> result = Optional.ofNullable(CommentEntityMapper.toDomain(entity));
        LoggerFactory.query().logQueryEnd(COMMENT_ENTITY, "[findCommentById] 주어진 ID에 해당하는 댓글 조회 종료. commentId=" + commentId, startTime);
        return result;
    }

    /**
     * 지정된 프로젝트의 루트 댓글 목록과 각 댓글의 답글 수를 페이지 단위로 반환합니다.
     *
     * @param projectId 댓글을 조회할 프로젝트의 ID
     * @param pageable 페이지네이션 및 정렬 정보
     * @return 각 루트 댓글과 해당 댓글의 답글 수를 포함하는 FindCommentWithReplyCountResponse의 페이지 객체
     */
    @Override
    public Page<FindCommentWithReplyCountResponse> findComments(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart(COMMENT_ENTITY, "[findComments] 댓글당 답글 수를 포함한 댓글 목록 조회 시작. projectId=" + projectId);
        int queryCount = 0;

        // 1단계: 루트 댓글 조회 (1개 쿼리)
        List<CommentEntity> commentEntities = queryFactory
                .selectFrom(comment)
                .where(
                        CommentFilterPredicate.projectIdEq(projectId),
                        CommentFilterPredicate.isRootComment()
                )
                .orderBy(CommentSortBuilder.createdAtDesc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        queryCount++; // 메인 쿼리

        // 2단계: 배치로 답글 수 조회 (1개 쿼리)
        List<Long> commentIds = commentEntities.stream().map(CommentEntity::getId).toList();
        Map<Long, Long> replyCounts = getReplyCountsBatch(commentIds);
        queryCount++; // 배치 쿼리

        // 3단계: DTO 조합 (메모리에서 처리)
        List<FindCommentWithReplyCountResponse> contents = commentEntities.stream()
                .map(entity -> new FindCommentWithReplyCountResponse(
                        CommentEntityMapper.toDomain(entity),
                        replyCounts.getOrDefault(entity.getId(), 0L)
                ))
                .toList();

        // 4단계: 총 개수 조회 (1개 쿼리)
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
        queryCount++; // 카운트 쿼리

        LoggerFactory.query().logQueryEnd(COMMENT_ENTITY, "[findComments] 댓글당 답글 수를 포함한 댓글 목록 조회 종료. projectId=" + projectId + ", queryCount=" + queryCount, startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 특정 프로젝트 내에서 지정된 부모 댓글에 대한 답글(대댓글) 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId 답글을 조회할 프로젝트의 ID
     * @param commentId 부모 댓글의 ID
     * @param pageable 페이지네이션 및 정렬 정보
     * @return 조회된 답글 목록과 전체 답글 수를 포함하는 Page 객체
     */
    @Override
    public Page<Comment> findReplyComments(Long projectId, Long commentId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart(COMMENT_ENTITY, "[findReplyComments] 해당 댓글에 대한 답글 목록 조회 시작. projectId=" + projectId + ", commentId=" + commentId);

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

        LoggerFactory.query().logQueryEnd(COMMENT_ENTITY, "[findReplyComments] 해당 댓글에 대한 답글 목록 조회 종료. projectId=" + projectId + ", commentId=" + commentId, startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 배치로 답글 수를 조회합니다.
     */
    private Map<Long, Long> getReplyCountsBatch(List<Long> commentIds) {
        if (commentIds.isEmpty()) return Collections.emptyMap();
        
        QCommentEntity child = new QCommentEntity("child");
        return queryFactory
                .select(child.parentCommentId, child.id.count())
                .from(child)
                .where(child.parentCommentId.in(commentIds))
                .groupBy(child.parentCommentId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(child.parentCommentId),
                        tuple -> tuple.get(child.id.count())
                ));
    }
}
