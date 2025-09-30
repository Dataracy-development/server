package com.dataracy.modules.common.dto.response;

import com.dataracy.modules.common.status.BaseSuccessCode;
import com.dataracy.modules.common.status.CommonSuccessStatus;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuccessResponseTest {

    @Test
    @DisplayName("of - BaseSuccessCode와 데이터로 SuccessResponse를 생성한다")
    void of_WithSuccessCodeAndData_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.OK;
        String data = "test data";

        // when
        SuccessResponse<String> result = SuccessResponse.of(successCode, data);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(200);
        assertThat(result.getCode()).isEqualTo("COMMON-200");
        assertThat(result.getMessage()).isEqualTo("성공입니다.");
        assertThat(result.getData()).isEqualTo("test data");
    }

    @Test
    @DisplayName("of - BaseSuccessCode만으로 SuccessResponse를 생성한다")
    void of_WithSuccessCodeOnly_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.CREATED;

        // when
        SuccessResponse<Void> result = SuccessResponse.of(successCode);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(201);
        assertThat(result.getCode()).isEqualTo("COMMON-201");
        assertThat(result.getMessage()).isEqualTo("생성에 성공했습니다.");
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("of - UserSuccessStatus로 SuccessResponse를 생성한다")
    void of_WithUserSuccessStatus_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = UserSuccessStatus.CREATED_USER;
        Long userId = 1L;

        // when
        SuccessResponse<Long> result = SuccessResponse.of(successCode, userId);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(201);
        assertThat(result.getCode()).isEqualTo("201");
        assertThat(result.getMessage()).isEqualTo("회원가입에 성공했습니다");
        assertThat(result.getData()).isEqualTo(1L);
    }

    @Test
    @DisplayName("of - null 데이터로 SuccessResponse를 생성한다")
    void of_WithNullData_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.NO_CONTENT;
        String data = null;

        // when
        SuccessResponse<String> result = SuccessResponse.of(successCode, data);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(204);
        assertThat(result.getCode()).isEqualTo("COMMON-204");
        assertThat(result.getMessage()).isEqualTo("성공입니다.");
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("of - 복잡한 객체 데이터로 SuccessResponse를 생성한다")
    void of_WithComplexData_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = UserSuccessStatus.OK_GET_USER_INFO;
        UserInfo data = new UserInfo(1L, "test@example.com", "TestUser");

        // when
        SuccessResponse<UserInfo> result = SuccessResponse.of(successCode, data);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(200);
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getMessage()).isEqualTo("유저 정보 조회가 완료되었습니다.");
        assertThat(result.getData()).isEqualTo(data);
    }

    // 테스트용 UserInfo 클래스
    private static class UserInfo {
        private final Long id;
        private final String email;
        private final String nickname;

        public UserInfo(Long id, String email, String nickname) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            UserInfo userInfo = (UserInfo) obj;
            return java.util.Objects.equals(id, userInfo.id) &&
                    java.util.Objects.equals(email, userInfo.email) &&
                    java.util.Objects.equals(nickname, userInfo.nickname);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, email, nickname);
        }
    }
}