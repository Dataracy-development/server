package com.dataracy.modules.like.adapter.query;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LikeQueryDslAdapterIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LikeQueryDslAdapter likeAdapter;

    private LikeEntity projectLike;
    private LikeEntity commentLike;
    private LikeEntity anotherUserLike;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        entityManager.createQuery("DELETE FROM LikeEntity").executeUpdate();
        entityManager.flush();

        // 테스트 데이터 생성
        projectLike = LikeEntity.of(1L, TargetType.PROJECT, 1L);
        entityManager.persist(projectLike);

        commentLike = LikeEntity.of(2L, TargetType.COMMENT, 1L);
        entityManager.persist(commentLike);

        anotherUserLike = LikeEntity.of(1L, TargetType.PROJECT, 2L);
        entityManager.persist(anotherUserLike);

        entityManager.flush();
    }

    @Nested
    @DisplayName("ReadLikePort 테스트")
    class ReadLikePortTest {

        @Test
        @DisplayName("사용자가 좋아요한 타겟 ID 목록 조회")
        void findLikedTargetIds_사용자_좋아요_타겟_ID_목록_조회_성공() {
            // given
            List<Long> targetIds = List.of(1L, 2L, 3L);

            // when
            List<Long> result = likeAdapter.findLikedTargetIds(1L, targetIds, TargetType.PROJECT);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).contains(1L);
        }

        @Test
        @DisplayName("다른 사용자의 좋아요는 조회되지 않음")
        void findLikedTargetIds_다른_사용자_좋아요_조회되지_않음() {
            // given
            List<Long> targetIds = List.of(1L, 2L, 3L);

            // when
            List<Long> result = likeAdapter.findLikedTargetIds(3L, targetIds, TargetType.PROJECT);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 타겟 ID 목록으로 조회 시 빈 결과 반환")
        void findLikedTargetIds_빈_타겟_ID_목록_조회_시_빈_결과() {
            // when
            List<Long> result = likeAdapter.findLikedTargetIds(1L, List.of(), TargetType.PROJECT);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null 타겟 ID 목록으로 조회 시 빈 결과 반환")
        void findLikedTargetIds_null_타겟_ID_목록_조회_시_빈_결과() {
            // when
            List<Long> result = likeAdapter.findLikedTargetIds(1L, null, TargetType.PROJECT);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null 사용자 ID로 조회 시 빈 결과 반환")
        void findLikedTargetIds_null_사용자_ID_조회_시_빈_결과() {
            // given
            List<Long> targetIds = List.of(1L, 2L, 3L);

            // when
            List<Long> result = likeAdapter.findLikedTargetIds(null, targetIds, TargetType.PROJECT);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("ValidateLikePort 테스트")
    class ValidateLikePortTest {

        @Test
        @DisplayName("사용자가 특정 타겟을 좋아요 했는지 확인 - 좋아요 한 경우")
        void isLikedTarget_좋아요_한_경우_true_반환() {
            // when
            boolean result = likeAdapter.isLikedTarget(1L, 1L, TargetType.PROJECT);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("사용자가 특정 타겟을 좋아요 했는지 확인 - 좋아요 안 한 경우")
        void isLikedTarget_좋아요_안_한_경우_false_반환() {
            // when
            boolean result = likeAdapter.isLikedTarget(1L, 3L, TargetType.PROJECT);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("다른 사용자의 좋아요는 false 반환")
        void isLikedTarget_다른_사용자_좋아요_false_반환() {
            // when
            boolean result = likeAdapter.isLikedTarget(3L, 1L, TargetType.PROJECT);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("다른 타겟 타입의 좋아요는 false 반환")
        void isLikedTarget_다른_타겟_타입_false_반환() {
            // when
            boolean result = likeAdapter.isLikedTarget(1L, 1L, TargetType.COMMENT);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회 시 false 반환")
        void isLikedTarget_존재하지_않는_사용자_ID_false_반환() {
            // when
            boolean result = likeAdapter.isLikedTarget(999L, 1L, TargetType.PROJECT);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 타겟 ID로 조회 시 false 반환")
        void isLikedTarget_존재하지_않는_타겟_ID_false_반환() {
            // when
            boolean result = likeAdapter.isLikedTarget(1L, 999L, TargetType.PROJECT);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("다양한 타겟 타입 테스트")
    class DifferentTargetTypesTest {

        @Test
        @DisplayName("프로젝트 타겟 좋아요 테스트")
        void project_target_like_test() {
            // when
            boolean isLiked = likeAdapter.isLikedTarget(1L, 1L, TargetType.PROJECT);
            List<Long> likedTargets = likeAdapter.findLikedTargetIds(1L, List.of(1L, 2L), TargetType.PROJECT);

            // then
            assertThat(isLiked).isTrue();
            assertThat(likedTargets).hasSize(1);
            assertThat(likedTargets).contains(1L);
        }

        @Test
        @DisplayName("댓글 타겟 좋아요 테스트")
        void comment_target_like_test() {
            // when
            boolean isLiked = likeAdapter.isLikedTarget(1L, 2L, TargetType.COMMENT);
            List<Long> likedTargets = likeAdapter.findLikedTargetIds(1L, List.of(1L, 2L), TargetType.COMMENT);

            // then
            assertThat(isLiked).isTrue();
            assertThat(likedTargets).hasSize(1);
            assertThat(likedTargets).contains(2L);
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("여러 사용자가 같은 타겟에 좋아요")
        void multiple_users_like_same_target() {
            // given - 이미 setUp에서 1L, 2L 사용자가 1L 프로젝트에 좋아요
            // when
            boolean user1Liked = likeAdapter.isLikedTarget(1L, 1L, TargetType.PROJECT);
            boolean user2Liked = likeAdapter.isLikedTarget(2L, 1L, TargetType.PROJECT);
            boolean user3Liked = likeAdapter.isLikedTarget(3L, 1L, TargetType.PROJECT);

            // then
            assertThat(user1Liked).isTrue();
            assertThat(user2Liked).isTrue();
            assertThat(user3Liked).isFalse();
        }

        @Test
        @DisplayName("한 사용자가 여러 타겟에 좋아요")
        void one_user_likes_multiple_targets() {
            // given - 이미 setUp에서 1L 사용자가 1L 프로젝트와 2L 댓글에 좋아요
            // when
            List<Long> projectLikes = likeAdapter.findLikedTargetIds(1L, List.of(1L, 2L, 3L), TargetType.PROJECT);
            List<Long> commentLikes = likeAdapter.findLikedTargetIds(1L, List.of(1L, 2L, 3L), TargetType.COMMENT);

            // then
            assertThat(projectLikes).hasSize(1);
            assertThat(projectLikes).contains(1L);
            assertThat(commentLikes).hasSize(1);
            assertThat(commentLikes).contains(2L);
        }
    }
}
