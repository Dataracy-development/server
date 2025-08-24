package com.dataracy.modules.comment.adapter.query;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.adapter.jpa.entity.QCommentEntity;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_SELF;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ReadCommentPortAdapterTest {

    private JPAQueryFactory queryFactory;
    private ReadCommentPortAdapter adapter;

    @BeforeEach
    void setup() {
        queryFactory = mock(JPAQueryFactory.class);
        adapter = new ReadCommentPortAdapter(queryFactory);
    }

    @Test
    @DisplayName("findCommentById → 조회 결과 없으면 Optional.empty 반환")
    void findCommentByIdNotFound() {
        // given
        @SuppressWarnings("unchecked")
        JPAQuery<CommentEntity> mockQuery = (JPAQuery<CommentEntity>) mock(JPAQuery.class);

        given(queryFactory.selectFrom(any(QCommentEntity.class))).willReturn(mockQuery);
        given(mockQuery.where(any(Predicate.class))).willReturn(mockQuery);
        given(mockQuery.fetchOne()).willReturn(null); // 조회 결과 없음

        // when
        Optional<Comment> result = adapter.findCommentById(1L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findComments → 결과 없으면 Page.empty 반환")
    @SuppressWarnings("unchecked")
    void findCommentsEmpty() {
        // given
        JPAQuery<Tuple> mockTupleQuery = mock(JPAQuery.class, RETURNS_SELF);
        JPAQuery<Long> mockCountQuery = mock(JPAQuery.class, RETURNS_SELF);

        // ------- 목록 쿼리 mocking -------
        given(queryFactory.select(any(Expression.class), any(Expression.class)))
                .willReturn(mockTupleQuery);

        given(mockTupleQuery.fetch()).willReturn(List.of()); // 결과 없음

        // ------- total count 쿼리 mocking -------
        given(queryFactory.select(any(Expression.class)))
                .willReturn(mockCountQuery);

        given(mockCountQuery.fetchOne()).willReturn(0L);

        // when
        Page<?> result = adapter.findComments(1L, PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("findReplyComments → 결과 없으면 Page.empty 반환")
    @SuppressWarnings("unchecked")
    void findReplyCommentsEmpty() {
        // given
        JPAQuery<Comment> mockCommentQuery = mock(JPAQuery.class, RETURNS_SELF);
        JPAQuery<Long> mockCountQuery = mock(JPAQuery.class, RETURNS_SELF);

        // ------- 목록 쿼리 mocking -------
        given(queryFactory.selectFrom(any(EntityPath.class)))
                .willReturn(mockCommentQuery);

        given(mockCommentQuery.fetch()).willReturn(List.of()); // 결과 없음

        // ------- total count 쿼리 mocking -------
        given(queryFactory.select(any(Expression.class)))
                .willReturn(mockCountQuery);

        given(mockCountQuery.fetchOne()).willReturn(0L);

        // when
        Page<?> result = adapter.findReplyComments(1L, 1L, PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("findCommentById → 조회 결과 있으면 Optional.of 반환")
    @SuppressWarnings("unchecked")
    void findCommentByIdFound() {
        // given
        JPAQuery<CommentEntity> mockQuery = (JPAQuery<CommentEntity>) mock(JPAQuery.class, RETURNS_SELF);

        CommentEntity entity = CommentEntity.builder()
                .id(1L)
                .content("hello world")
                .build();

        given(queryFactory.selectFrom(any(QCommentEntity.class))).willReturn(mockQuery);
        given(mockQuery.where(any(Predicate.class))).willReturn(mockQuery);
        given(mockQuery.fetchOne()).willReturn(entity);

        // when
        Optional<Comment> result = adapter.findCommentById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getContent()).isEqualTo("hello world");
    }

    @Test
    @DisplayName("findComments → 결과 있으면 Page 반환")
    @SuppressWarnings("unchecked")
    void findCommentsFound() {
        // given
        JPAQuery<Tuple> mockTupleQuery = mock(JPAQuery.class, RETURNS_SELF);
        JPAQuery<Long> mockCountQuery = mock(JPAQuery.class, RETURNS_SELF);

        Tuple mockTuple = mock(Tuple.class);

        CommentEntity entity = CommentEntity.builder()
                .id(100L)
                .projectId(1L)
                .userId(10L)
                .content("first comment")
                .parentCommentId(null)
                .likeCount(0L)
                .build();

        // select 쿼리 mocking
        given(queryFactory.select(any(Expression.class), any(Expression.class)))
                .willReturn(mockTupleQuery);
        given(mockTupleQuery.fetch()).willReturn(List.of(mockTuple));

        // tuple.get(...) → CommentEntity
        given(mockTuple.get(any(Expression.class))).willReturn(entity);
        // tuple.get(1, Long.class) → 답글 수
        given(mockTuple.get(1, Long.class)).willReturn(5L);

        // count 쿼리 mocking
        given(queryFactory.select(any(Expression.class))).willReturn(mockCountQuery);
        given(mockCountQuery.fetchOne()).willReturn(1L);

        // when
        Page<?> result = adapter.findComments(1L, PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        FindCommentWithReplyCountResponse response =
                (FindCommentWithReplyCountResponse) result.getContent().get(0);

        assertThat(response.comment()).isNotNull();
        assertThat(response.comment().getId()).isEqualTo(100L);
        assertThat(response.comment().getContent()).isEqualTo("first comment");
        assertThat(response.replyCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("findReplyComments → 결과 있으면 Page 반환")
    @SuppressWarnings("unchecked")
    void findReplyCommentsFound() {
        // given
        JPAQuery<CommentEntity> mockCommentQuery = (JPAQuery<CommentEntity>) mock(JPAQuery.class, RETURNS_SELF);
        JPAQuery<Long> mockCountQuery = mock(JPAQuery.class, RETURNS_SELF);

        CommentEntity entity = CommentEntity.builder()
                .id(200L)
                .content("reply content")
                .build();

        given(queryFactory.selectFrom(any(EntityPath.class))).willReturn(mockCommentQuery);
        given(mockCommentQuery.fetch()).willReturn(List.of(entity));

        given(queryFactory.select(any(Expression.class))).willReturn(mockCountQuery);
        given(mockCountQuery.fetchOne()).willReturn(1L);

        // when
        Page<?> result = adapter.findReplyComments(1L, 1L, PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
    }
}
