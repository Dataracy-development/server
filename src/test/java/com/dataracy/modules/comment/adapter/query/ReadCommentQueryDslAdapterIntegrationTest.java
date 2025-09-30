package com.dataracy.modules.comment.adapter.query;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;import org.springframework.data.domain.PageRequest;
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
class ReadCommentQueryDslAdapterIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ReadCommentPortAdapter readCommentAdapter;

    private UserEntity savedUser;
    private ProjectEntity savedProject;
    private CommentEntity rootComment;
    private CommentEntity replyComment;
    private CommentEntity anotherRootComment;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        entityManager.createQuery("DELETE FROM CommentEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM ProjectEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM UserEntity").executeUpdate();
        entityManager.flush();

        // 테스트 데이터 생성
        savedUser = UserEntity.builder()
                .provider(ProviderType.LOCAL)
                .role(RoleType.ROLE_USER)
                .email("test@example.com")
                .nickname("테스트유저")
                .authorLevelId(1L)
                .profileImageUrl("https://example.com/profile.jpg")
                .introductionText("테스트 사용자입니다")
                .isAdTermsAgreed(false)
                .isDeleted(false)
                .build();
        entityManager.persist(savedUser);

        savedProject = ProjectEntity.builder()
                .title("테스트 프로젝트")
                .content("테스트 내용")
                .userId(savedUser.getId())
                .topicId(1L)
                .dataSourceId(1L)
                .analysisPurposeId(1L)
                .authorLevelId(1L)
                .isContinue(false)
                .isDeleted(false)
                .build();
        entityManager.persist(savedProject);

        rootComment = CommentEntity.of(savedProject.getId(), savedUser.getId(), "루트 댓글 내용", null);
        entityManager.persist(rootComment);

        replyComment = CommentEntity.of(savedProject.getId(), savedUser.getId(), "답글 내용", rootComment.getId());
        entityManager.persist(replyComment);

        anotherRootComment = CommentEntity.of(savedProject.getId(), savedUser.getId(), "다른 루트 댓글", null);
        entityManager.persist(anotherRootComment);

        entityManager.flush();
    }

    @Nested
    @DisplayName("ReadCommentPort 테스트")
    class ReadCommentPortTest {

        @Test
        @DisplayName("댓글 ID로 댓글 조회")
        void findCommentById_댓글_ID로_댓글_조회_성공() {
            // when
            Optional<Comment> result = readCommentAdapter.findCommentById(rootComment.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getContent()).isEqualTo("루트 댓글 내용");
            assertThat(result.get().getUserId()).isEqualTo(savedUser.getId());
        }

        @Test
        @DisplayName("존재하지 않는 댓글 ID로 조회 시 빈 Optional 반환")
        void findCommentById_존재하지_않는_댓글_ID_빈_Optional_반환() {
            // when
            Optional<Comment> result = readCommentAdapter.findCommentById(999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("프로젝트 ID로 루트 댓글 목록 조회")
        void findComments_프로젝트_ID로_루트_댓글_목록_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(savedProject.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2); // rootComment, anotherRootComment (replyComment 제외)
            
            // 답글 수 확인
            FindCommentWithReplyCountResponse rootCommentResponse = result.getContent().stream()
                    .filter(response -> response.comment().getContent().equals("루트 댓글 내용"))
                    .findFirst()
                    .orElse(null);
            assertThat(rootCommentResponse).isNotNull();
            assertThat(rootCommentResponse.replyCount()).isEqualTo(1); // replyComment가 답글
        }

        @Test
        @DisplayName("페이지네이션으로 루트 댓글 조회")
        void findComments_페이지네이션으로_루트_댓글_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 1);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(savedProject.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("두 번째 페이지 루트 댓글 조회")
        void findComments_두_번째_페이지_루트_댓글_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(1, 1);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(savedProject.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID로 댓글 조회 시 빈 결과")
        void findComments_존재하지_않는_프로젝트_ID_빈_결과() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(999L, pageable);

            // then
            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("부모 댓글 ID로 답글 목록 조회")
        void findReplyComments_부모_댓글_ID로_답글_목록_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Comment> result = readCommentAdapter.findReplyComments(savedProject.getId(), rootComment.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getContent()).isEqualTo("답글 내용");
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("페이지네이션으로 답글 조회")
        void findReplyComments_페이지네이션으로_답글_조회_성공() {
            // given - 추가 답글 생성
            CommentEntity secondReply = CommentEntity.of(savedProject.getId(), savedUser.getId(), "두 번째 답글", rootComment.getId());
            entityManager.persist(secondReply);
            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 1);

            // when
            Page<Comment> result = readCommentAdapter.findReplyComments(savedProject.getId(), rootComment.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("답글이 없는 부모 댓글의 답글 조회 시 빈 결과")
        void findReplyComments_답글이_없는_부모_댓글_빈_결과() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Comment> result = readCommentAdapter.findReplyComments(savedProject.getId(), anotherRootComment.getId(), pageable);

            // then
            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID로 답글 조회 시 빈 결과")
        void findReplyComments_존재하지_않는_프로젝트_ID_빈_결과() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Comment> result = readCommentAdapter.findReplyComments(999L, rootComment.getId(), pageable);

            // then
            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("여러 답글이 있는 댓글의 답글 수 계산")
        void multiple_replies_count_calculation() {
            // given - 추가 답글들 생성
            CommentEntity reply2 = CommentEntity.of(savedProject.getId(), savedUser.getId(), "답글 2", rootComment.getId());
            CommentEntity reply3 = CommentEntity.of(savedProject.getId(), savedUser.getId(), "답글 3", rootComment.getId());
            entityManager.persist(reply2);
            entityManager.persist(reply3);
            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(savedProject.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            
            // 루트 댓글의 답글 수 확인
            FindCommentWithReplyCountResponse rootCommentResponse = result.getContent().stream()
                    .filter(response -> response.comment().getContent().equals("루트 댓글 내용"))
                    .findFirst()
                    .orElse(null);
            assertThat(rootCommentResponse).isNotNull();
            assertThat(rootCommentResponse.replyCount()).isEqualTo(3); // replyComment, reply2, reply3
        }

        @Test
        @DisplayName("여러 프로젝트의 댓글 분리")
        void comments_from_different_projects_separation() {
            // given - 다른 프로젝트 생성
            ProjectEntity anotherProject = ProjectEntity.builder()
                    .title("다른 프로젝트")
                    .content("다른 내용")
                    .userId(savedUser.getId())
                    .topicId(1L)
                    .dataSourceId(1L)
                    .analysisPurposeId(1L)
                    .authorLevelId(1L)
                    .isContinue(false)
                    .isDeleted(false)
                    .build();
            entityManager.persist(anotherProject);

            CommentEntity anotherProjectComment = CommentEntity.of(anotherProject.getId(), savedUser.getId(), "다른 프로젝트 댓글", null);
            entityManager.persist(anotherProjectComment);
            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 10);

            // when - 원래 프로젝트의 댓글만 조회
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(savedProject.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2); // 원래 프로젝트의 댓글만
            assertThat(result.getContent().stream()
                    .anyMatch(response -> response.comment().getContent().equals("다른 프로젝트 댓글")))
                    .isFalse();
        }

        @Test
        @DisplayName("여러 사용자의 댓글")
        void comments_from_different_users() {
            // given - 다른 사용자 생성
            UserEntity anotherUser = UserEntity.builder()
                    .provider(ProviderType.LOCAL)
                    .role(RoleType.ROLE_USER)
                    .email("another@example.com")
                    .nickname("다른유저")
                    .authorLevelId(1L)
                    .profileImageUrl("https://example.com/profile2.jpg")
                    .introductionText("다른 사용자입니다")
                    .isAdTermsAgreed(false)
                    .isDeleted(false)
                    .build();
            entityManager.persist(anotherUser);

            CommentEntity anotherUserComment = CommentEntity.of(savedProject.getId(), anotherUser.getId(), "다른 사용자 댓글", null);
            entityManager.persist(anotherUserComment);
            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(savedProject.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(3); // rootComment, anotherRootComment, anotherUserComment
            
            // 다른 사용자의 댓글 확인
            boolean foundAnotherUserComment = result.getContent().stream()
                    .anyMatch(response -> response.comment().getContent().equals("다른 사용자 댓글"));
            assertThat(foundAnotherUserComment).isTrue();
        }

        @Test
        @DisplayName("대댓글의 답글 수는 0")
        void reply_comments_have_zero_reply_count() {
            // given - 대댓글에 대한 답글 생성
            CommentEntity replyToReply = CommentEntity.of(savedProject.getId(), savedUser.getId(), "답글의 답글", replyComment.getId());
            entityManager.persist(replyToReply);
            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(savedProject.getId(), pageable);

            // then
            assertThat(result).isNotEmpty();
            
            // 루트 댓글의 답글 수는 여전히 1 (replyComment만)
            FindCommentWithReplyCountResponse rootCommentResponse = result.getContent().stream()
                    .filter(response -> response.comment().getContent().equals("루트 댓글 내용"))
                    .findFirst()
                    .orElse(null);
            assertThat(rootCommentResponse).isNotNull();
            assertThat(rootCommentResponse.replyCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("빈 댓글 목록 처리")
        void empty_comment_list_handling() {
            // given - 새로운 프로젝트 생성 (댓글 없음)
            ProjectEntity emptyProject = ProjectEntity.builder()
                    .title("빈 프로젝트")
                    .content("빈 내용")
                    .userId(savedUser.getId())
                    .topicId(1L)
                    .dataSourceId(1L)
                    .analysisPurposeId(1L)
                    .authorLevelId(1L)
                    .isContinue(false)
                    .isDeleted(false)
                    .build();
            entityManager.persist(emptyProject);
            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<FindCommentWithReplyCountResponse> result = readCommentAdapter.findComments(emptyProject.getId(), pageable);

            // then
            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(0);
        }
    }
}
