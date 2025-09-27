package com.dataracy.modules.common.dto.response;

import com.dataracy.modules.common.status.BaseSuccessCode;
import com.dataracy.modules.common.status.CommonSuccessStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SuccessResponseTest {

    @Test
    @DisplayName("of(BaseSuccessCode, T) - 데이터와 함께 성공 응답 생성")
    void of_WithData_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.OK;
        String testData = "test data";

        // when
        SuccessResponse<String> response = SuccessResponse.of(successCode, testData);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getCode()).isEqualTo(successCode.getCode());
        assertThat(response.getMessage()).isEqualTo(successCode.getMessage());
        assertThat(response.getData()).isEqualTo(testData);
    }

    @Test
    @DisplayName("of(BaseSuccessCode) - 데이터 없이 성공 응답 생성")
    void of_WithoutData_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.OK;

        // when
        SuccessResponse<Void> response = SuccessResponse.of(successCode);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getCode()).isEqualTo(successCode.getCode());
        assertThat(response.getMessage()).isEqualTo(successCode.getMessage());
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("of(BaseSuccessCode, T) - null 데이터와 함께 성공 응답 생성")
    void of_WithNullData_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.OK;

        // when
        SuccessResponse<String> response = SuccessResponse.of(successCode, null);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getCode()).isEqualTo(successCode.getCode());
        assertThat(response.getMessage()).isEqualTo(successCode.getMessage());
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("of(BaseSuccessCode, T) - 복잡한 객체 데이터와 함께 성공 응답 생성")
    void of_WithComplexData_CreatesSuccessResponse() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.OK;
        TestData testData = new TestData("name", 25);

        // when
        SuccessResponse<TestData> response = SuccessResponse.of(successCode, testData);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getCode()).isEqualTo(successCode.getCode());
        assertThat(response.getMessage()).isEqualTo(successCode.getMessage());
        assertThat(response.getData()).isEqualTo(testData);
        assertThat(response.getData().getName()).isEqualTo("name");
        assertThat(response.getData().getAge()).isEqualTo(25);
    }

    @Test
    @DisplayName("builder() - 빌더 패턴으로 성공 응답 생성")
    void builder_CreatesSuccessResponse() {
        // when
        SuccessResponse<String> response = SuccessResponse.<String>builder()
                .httpStatus(200)
                .code("SUCCESS")
                .message("Operation completed successfully")
                .data("test data")
                .build();

        // then
        assertThat(response.getHttpStatus()).isEqualTo(200);
        assertThat(response.getCode()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo("Operation completed successfully");
        assertThat(response.getData()).isEqualTo("test data");
    }

    @Test
    @DisplayName("builder() - 빌더 패턴으로 데이터 없는 성공 응답 생성")
    void builder_WithoutData_CreatesSuccessResponse() {
        // when
        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .httpStatus(201)
                .code("CREATED")
                .message("Resource created successfully")
                .data(null)
                .build();

        // then
        assertThat(response.getHttpStatus()).isEqualTo(201);
        assertThat(response.getCode()).isEqualTo("CREATED");
        assertThat(response.getMessage()).isEqualTo("Resource created successfully");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("of(BaseSuccessCode, T) - 제네릭 타입 검증")
    void of_GenericTypeVerification() {
        // given
        BaseSuccessCode successCode = CommonSuccessStatus.OK;
        Integer intData = 42;
        Boolean boolData = true;

        // when
        SuccessResponse<Integer> intResponse = SuccessResponse.of(successCode, intData);
        SuccessResponse<Boolean> boolResponse = SuccessResponse.of(successCode, boolData);

        // then
        assertThat(intResponse.getData()).isInstanceOf(Integer.class);
        assertThat(intResponse.getData()).isEqualTo(42);
        assertThat(boolResponse.getData()).isInstanceOf(Boolean.class);
        assertThat(boolResponse.getData()).isTrue();
    }

    // 테스트용 내부 클래스
    private static class TestData {
        private final String name;
        private final int age;

        public TestData(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestData testData = (TestData) obj;
            return age == testData.age && name.equals(testData.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, age);
        }
    }
}
