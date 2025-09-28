package com.dataracy.modules.user.application.service.integration;

// import com.dataracy.modules.user.application.port.in.command.SelfSignUpUseCase;
// import com.dataracy.modules.user.application.port.in.query.GetUserProfileUseCase;
// import com.dataracy.modules.user.application.dto.request.SelfSignUpRequest;
// import com.dataracy.modules.user.application.dto.response.UserProfileResponse;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.enums.ProviderType;
// import com.dataracy.modules.user.domain.enums.AuthorLevelType;
// import com.dataracy.modules.user.domain.enums.OccupationType;
// import com.dataracy.modules.user.domain.enums.DomainTopicType;
// import com.dataracy.modules.user.domain.enums.VisitSourceType;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.common.test.support.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User 서비스 통합 테스트
 * 
 * 사용자 관련 비즈니스 로직의 실제 데이터베이스 통합을 테스트합니다:
 * - 사용자 회원가입 프로세스
 * - 사용자 프로필 조회
 * - 데이터베이스 트랜잭션 처리
 * - 복잡한 비즈니스 규칙 검증
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    // @Autowired
    // private SelfSignUpUseCase selfSignUpUseCase;

    // @Autowired
    // private GetUserProfileUseCase getUserProfileUseCase;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 정리
        userJpaRepository.deleteAll();
    }

    // @Test
    // @DisplayName("자체 회원가입 → 데이터베이스에 저장되고 프로필 조회 가능")
    // void selfSignUp_ShouldBeSavedAndRetrievable() {
    //     // 통합 테스트 구현 예시
    // }

    // @Test
    // @DisplayName("사용자 프로필 조회 → 실제 저장된 데이터 반환")
    // void getUserProfile_ShouldReturnActualData() {
    //     // 통합 테스트 구현 예시
    // }

    // @Test
    // @DisplayName("중복 이메일 회원가입 → 예외 발생")
    // void signUpWithDuplicateEmail_ShouldThrowException() {
    //     // 통합 테스트 구현 예시
    // }

    // @Test
    // @DisplayName("트랜잭션 롤백 테스트 → 예외 발생 시 데이터 변경사항 롤백")
    // void transactionRollback_WhenExceptionOccurs_ShouldRollbackChanges() {
    //     // 통합 테스트 구현 예시
    // }

    // @Test
    // @DisplayName("사용자 목록 조회 → 실제 저장된 사용자들 반환")
    // void getUserList_ShouldReturnActualUsers() {
    //     // 통합 테스트 구현 예시
    // }
}
