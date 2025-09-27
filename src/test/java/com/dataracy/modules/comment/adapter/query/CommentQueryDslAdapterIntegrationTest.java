package com.dataracy.modules.comment.adapter.query;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentQueryDslAdapterIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ReadCommentPortAdapter commentAdapter;

    private CommentEntity rootComment;
    private CommentEntity replyComment;
    private CommentEntity anotherRootComment;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        entityManager.createQuery("DELETE FROM CommentEntity").executeUpdate();
        entityManager.flush();

        // 테스트 데이터 생성
        rootComment = CommentEntity.of(1L, 1L, "루트 댓글", null);
        entityManager.persist(rootComment);

        replyComment = CommentEntity.of(1L, 2L, "답글", rootComment.getId());
        entityManager.persist(replyComment);

        anotherRootComment = CommentEntity.of(1L, 3L, "다른 루트 댓글", null);
        entityManager.persist(anotherRootComment);

        entityManager.flush();
    }

    @Nested
    @DisplayName("ReadCommentPort 테스트")
    class ReadCommentPortTest {

        @Test
        @DisplayName("ID로 댓글 조회")
        void findCommentById_댓글_조회_성공() {
            // when
            Optional<Comment> result = commentAdapter.findCommentById(rootComment.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getContent()).isEqualTo("루트 댓글");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 댓글 조회 시 빈 결과 반환")
        void findCommentById_존재하지_않는_ID_조회_시_빈_결과() {
            // when
            Optional<Comment> result = commentAdapter.findCommentById(999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("프로젝트의 루트 댓글 목록 조회")
        void findComments_프로젝트_루트_댓글_목록_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = commentAdapter.findComments(1L, pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2); // rootComment, anotherRootComment
        }

        @Test
        @DisplayName("프로젝트의 루트 댓글 목록 조회 - 답글 수 포함")
        void findComments_프로젝트_루트_댓글_목록_답글_수_포함_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = commentAdapter.findComments(1L, pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
            
            // 답글이 있는 댓글과 없는 댓글 확인
            FindCommentWithReplyCountResponse commentWithReply = result.getContent().stream()
                    .filter(c -> c.comment().getContent().equals("루트 댓글"))
                    .findFirst()
                    .orElse(null);
            assertThat(commentWithReply).isNotNull();
            assertThat(commentWithReply.replyCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트의 댓글 조회 시 빈 결과 반환")
        void findComments_존재하지_않는_프로젝트_댓글_조회_시_빈_결과() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = commentAdapter.findComments(999L, pageable);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("특정 댓글의 답글 목록 조회")
        void findReplyComments_특정_댓글_답글_목록_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Comment> result = commentAdapter.findReplyComments(1L, rootComment.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getContent()).isEqualTo("답글");
        }

        @Test
        @DisplayName("답글이 없는 댓글의 답글 목록 조회 시 빈 결과 반환")
        void findReplyComments_답글_없는_댓글_답글_목록_조회_시_빈_결과() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Comment> result = commentAdapter.findReplyComments(1L, anotherRootComment.getId(), pageable);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 댓글의 답글 목록 조회 시 빈 결과 반환")
        void findReplyComments_존재하지_않는_댓글_답글_목록_조회_시_빈_결과() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Comment> result = commentAdapter.findReplyComments(1L, 999L, pageable);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("페이지네이션 테스트")
    class PaginationTest {

        @Test
        @DisplayName("페이지네이션 - 첫 번째 페이지")
        void findComments_페이지네이션_첫_번째_페이지_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 1);

            // when
            Page<FindCommentWithReplyCountResponse> result = commentAdapter.findComments(1L, pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("페이지네이션 - 두 번째 페이지")
        void findComments_페이지네이션_두_번째_페이지_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(1, 1);

            // when
            Page<FindCommentWithReplyCountResponse> result = commentAdapter.findComments(1L, pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("페이지네이션 - 페이지 크기보다 작은 데이터")
        void findComments_페이지네이션_페이지_크기보다_작은_데이터_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = commentAdapter.findComments(1L, pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("여러 답글이 있는 댓글 테스트")
        void multiple_replies_test() {
            // given - 추가 답글 생성
            CommentEntity secondReply = CommentEntity.of(1L, 4L, "두 번째 답글", rootComment.getId());
            entityManager.persist(secondReply);
            entityManager.flush();

            // when
            Pageable pageable = PageRequest.of(0, 10);
            Page<FindCommentWithReplyCountResponse> comments = commentAdapter.findComments(1L, pageable);
            Page<Comment> replies = commentAdapter.findReplyComments(1L, rootComment.getId(), pageable);

            // then
            FindCommentWithReplyCountResponse rootCommentWithReplies = comments.getContent().stream()
                    .filter(c -> c.comment().getContent().equals("루트 댓글"))
                    .findFirst()
                    .orElse(null);
            assertThat(rootCommentWithReplies).isNotNull();
            assertThat(rootCommentWithReplies.replyCount()).isEqualTo(2L);
            
            assertThat(replies.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("다른 프로젝트의 댓글은 조회되지 않음")
        void different_project_comments_not_retrieved() {
            // given - 다른 프로젝트의 댓글 생성
            CommentEntity otherProjectComment = CommentEntity.of(2L, 1L, "다른 프로젝트 댓글", null);
            entityManager.persist(otherProjectComment);
            entityManager.flush();

            // when
            Pageable pageable = PageRequest.of(0, 10);
            Page<FindCommentWithReplyCountResponse> result = commentAdapter.findComments(1L, pageable);

            // then
            assertThat(result.getContent()).hasSize(2); // 원래 댓글들만
            assertThat(result.getContent()).noneMatch(c -> c.comment().getContent().equals("다른 프로젝트 댓글"));
        }
    }
}
