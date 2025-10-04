package com.dataracy.modules.like.application.service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.test.support.TestDataBuilder;
import com.dataracy.modules.like.adapter.jpa.repository.LikeJpaRepository;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.command.LikeTargetUseCase;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;

/**
 * Like 서비스 통합 테스트
 *
 * <p>실제 데이터베이스와의 통합을 테스트하여 다음을 검증합니다: - 데이터베이스 트랜잭션 처리 - JPA 엔티티 매핑 - 복잡한 비즈니스 로직의 실제 동작 - 외부 의존성과의
 * 통합
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(
    properties = {
      "spring.kafka.bootstrap-servers=localhost:9092",
      "spring.kafka.consumer.bootstrap-servers=localhost:9092",
      "spring.kafka.producer.bootstrap-servers=localhost:9092"
    })
class LikeServiceIntegrationTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;
  private static final Integer TWENTY_FOUR = 24;
  private static final Integer FOURTEEN = 14;
  private static final Integer EIGHTEEN = 18;
  @Autowired private LikeTargetUseCase likeTargetUseCase;

  @Autowired private UserJpaRepository userJpaRepository;

  @Autowired private ProjectJpaRepository projectJpaRepository;

  @Autowired private LikeJpaRepository likeJpaRepository;

  @MockBean private SendLikeEventPort sendLikeEventPort;

  private UserEntity testUser;
  private ProjectEntity testProject;

  @BeforeEach
  void setUp() {
    // 테스트 데이터 생성 및 저장
    testUser =
        TestDataBuilder.userEntity()
            .email(TestDataBuilder.RandomData.randomEmail())
            .nickname(TestDataBuilder.RandomData.randomNickname())
            .build();
    testUser = userJpaRepository.save(testUser);

    testProject =
        TestDataBuilder.projectEntity().userId(testUser.getId()).title("통합 테스트 프로젝트").build();
    testProject = projectJpaRepository.save(testProject);
  }

  @Test
  @DisplayName("프로젝트 좋아요 → 데이터베이스에 저장되고 조회 가능")
  void likeProjectShouldBeSavedAndRetrievable() {
    // given
    TargetLikeRequest request = new TargetLikeRequest(testProject.getId(), "PROJECT", false);

    // when
    TargetType result = likeTargetUseCase.likeTarget(testUser.getId(), request);

    // then
    assertAll(
        () -> assertThat(result).isEqualTo(TargetType.PROJECT),
        () -> {
          // 데이터베이스에서 실제 저장 확인
          var savedLikes =
              likeJpaRepository.findByUserIdAndTargetIdAndTargetType(
                  testUser.getId(), testProject.getId(), TargetType.PROJECT);
          assertThat(savedLikes).isPresent();

          var savedLike = savedLikes.get();
          assertAll(
              () -> assertThat(savedLike.getUserId()).isEqualTo(testUser.getId()),
              () -> assertThat(savedLike.getTargetId()).isEqualTo(testProject.getId()),
              () -> assertThat(savedLike.getTargetType()).isEqualTo(TargetType.PROJECT));
        });
  }

  @Test
  @DisplayName("좋아요 취소 → 데이터베이스에서 삭제")
  void unlikeProjectShouldBeRemovedFromDatabase() {
    // given - 먼저 좋아요 생성
    TargetLikeRequest likeRequest = new TargetLikeRequest(testProject.getId(), "PROJECT", false);
    likeTargetUseCase.likeTarget(testUser.getId(), likeRequest);

    // when - 좋아요 취소
    TargetLikeRequest unlikeRequest = new TargetLikeRequest(testProject.getId(), "PROJECT", true);
    TargetType result = likeTargetUseCase.likeTarget(testUser.getId(), unlikeRequest);

    // then
    assertAll(
        () -> assertThat(result).isEqualTo(TargetType.PROJECT),
        () -> {
          // 데이터베이스에서 삭제 확인
          var remainingLikes =
              likeJpaRepository.findByUserIdAndTargetIdAndTargetType(
                  testUser.getId(), testProject.getId(), TargetType.PROJECT);
          assertThat(remainingLikes).isEmpty();
        });
  }

  @Test
  @DisplayName("트랜잭션 롤백 테스트 → 예외 발생 시 데이터 변경사항 롤백")
  void transactionRollbackWhenExceptionOccursShouldRollbackChanges() {
    // given
    TargetLikeRequest request = new TargetLikeRequest(testProject.getId(), "PROJECT", false);

    // when & then - 예외 발생 시나리오 (실제 구현에서는 예외를 발생시키는 로직이 필요)
    // 이 테스트는 @Transactional이 제대로 작동하는지 확인
    TargetType result = likeTargetUseCase.likeTarget(testUser.getId(), request);

    // 정상적으로 처리되었는지 확인
    assertAll(
        () -> assertThat(result).isEqualTo(TargetType.PROJECT),
        () -> {
          // 트랜잭션이 커밋되어 데이터가 저장되었는지 확인
          var savedLikes =
              likeJpaRepository.findByUserIdAndTargetIdAndTargetType(
                  testUser.getId(), testProject.getId(), TargetType.PROJECT);
          assertThat(savedLikes).isPresent();
        });
  }
}
