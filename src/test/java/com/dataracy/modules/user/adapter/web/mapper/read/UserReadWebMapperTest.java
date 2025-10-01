package com.dataracy.modules.user.adapter.web.mapper.read;

import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserReadWebMapperTest {

    private UserReadWebMapper userReadWebMapper;

    @BeforeEach
    void setUp() {
        userReadWebMapper = new UserReadWebMapper();
    }

    @Test
    @DisplayName("toWebDto - GetUserInfoResponse를 GetUserInfoWebResponse로 변환한다")
    void toWebDto_ConvertsGetUserInfoResponseToWebResponse() {
        // given
        GetUserInfoResponse responseDto = new GetUserInfoResponse(
                1L,
                RoleType.ROLE_USER,
                "test@example.com",
                "TestUser",
                1L,
                "초급",
                2L,
                "개발자",
                List.of(1L, 2L, 3L),
                List.of("웹개발", "데이터분석", "AI"),
                1L,
                "구글",
                "https://example.com/profile.jpg",
                "안녕하세요. 테스트 사용자입니다."
        );

        // when
        GetUserInfoWebResponse result = userReadWebMapper.toWebDto(responseDto);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(1L),
                () -> assertThat(result.role()).isEqualTo(RoleType.ROLE_USER),
                () -> assertThat(result.email()).isEqualTo("test@example.com"),
                () -> assertThat(result.nickname()).isEqualTo("TestUser"),
                () -> assertThat(result.authorLevelId()).isEqualTo(1L),
                () -> assertThat(result.authorLevelLabel()).isEqualTo("초급"),
                () -> assertThat(result.occupationId()).isEqualTo(2L),
                () -> assertThat(result.occupationLabel()).isEqualTo("개발자"),
                () -> assertThat(result.topicIds()).containsExactly(1L, 2L, 3L),
                () -> assertThat(result.topicLabels()).containsExactly("웹개발", "데이터분석", "AI"),
                () -> assertThat(result.visitSourceId()).isEqualTo(1L),
                () -> assertThat(result.visitSourceLabel()).isEqualTo("구글"),
                () -> assertThat(result.profileImageUrl()).isEqualTo("https://example.com/profile.jpg"),
                () -> assertThat(result.introductionText()).isEqualTo("안녕하세요. 테스트 사용자입니다.")
        );
    }

    @Test
    @DisplayName("toWebDto - null 값들이 포함된 GetUserInfoResponse를 변환한다")
    void toWebDto_WithNullValues_ConvertsResponse() {
        // given
        GetUserInfoResponse responseDto = new GetUserInfoResponse(
                2L,
                RoleType.ROLE_ADMIN,
                "admin@example.com",
                "AdminUser",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // when
        GetUserInfoWebResponse result = userReadWebMapper.toWebDto(responseDto);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(2L),
                () -> assertThat(result.role()).isEqualTo(RoleType.ROLE_ADMIN),
                () -> assertThat(result.email()).isEqualTo("admin@example.com"),
                () -> assertThat(result.nickname()).isEqualTo("AdminUser"),
                () -> assertThat(result.authorLevelId()).isNull(),
                () -> assertThat(result.authorLevelLabel()).isNull(),
                () -> assertThat(result.occupationId()).isNull(),
                () -> assertThat(result.occupationLabel()).isNull(),
                () -> assertThat(result.topicIds()).isNull(),
                () -> assertThat(result.topicLabels()).isNull(),
                () -> assertThat(result.visitSourceId()).isNull(),
                () -> assertThat(result.visitSourceLabel()).isNull(),
                () -> assertThat(result.profileImageUrl()).isNull(),
                () -> assertThat(result.introductionText()).isNull()
        );
    }

    @Test
    @DisplayName("toWebDto - 빈 리스트가 포함된 GetUserInfoResponse를 변환한다")
    void toWebDto_WithEmptyLists_ConvertsResponse() {
        // given
        GetUserInfoResponse responseDto = new GetUserInfoResponse(
                3L,
                RoleType.ROLE_USER,
                "empty@example.com",
                "EmptyUser",
                1L,
                "중급",
                1L,
                "디자이너",
                List.of(),
                List.of(),
                2L,
                "네이버",
                "https://example.com/empty.jpg",
                ""
        );

        // when
        GetUserInfoWebResponse result = userReadWebMapper.toWebDto(responseDto);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(3L),
                () -> assertThat(result.role()).isEqualTo(RoleType.ROLE_USER),
                () -> assertThat(result.email()).isEqualTo("empty@example.com"),
                () -> assertThat(result.nickname()).isEqualTo("EmptyUser"),
                () -> assertThat(result.authorLevelId()).isEqualTo(1L),
                () -> assertThat(result.authorLevelLabel()).isEqualTo("중급"),
                () -> assertThat(result.occupationId()).isEqualTo(1L),
                () -> assertThat(result.occupationLabel()).isEqualTo("디자이너"),
                () -> assertThat(result.topicIds()).isEmpty(),
                () -> assertThat(result.topicLabels()).isEmpty(),
                () -> assertThat(result.visitSourceId()).isEqualTo(2L),
                () -> assertThat(result.visitSourceLabel()).isEqualTo("네이버"),
                () -> assertThat(result.profileImageUrl()).isEqualTo("https://example.com/empty.jpg"),
                () -> assertThat(result.introductionText()).isEmpty()
        );
    }

    @Test
    @DisplayName("toWebDto - null GetUserInfoResponse를 변환한다")
    void toWebDto_WithNullResponse_ConvertsResponse() {
        // given
        GetUserInfoResponse responseDto = null;

        // when & then
        NullPointerException exception = catchThrowableOfType(
                () -> userReadWebMapper.toWebDto(responseDto),
                NullPointerException.class
        );
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull()
        );
    }
}