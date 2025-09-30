package com.dataracy.modules.reference.domain.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ReferenceErrorStatus 테스트
 */
class ReferenceErrorStatusTest {

    @Test
    @DisplayName("NOT_FOUND_TOPIC_NAME 상태 확인")
    void notFoundTopicName_상태확인() {
        // when
        ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME;

        // then
        assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(status.getCode()).isEqualTo("REFERENCE-001");
        assertThat(status.getMessage()).isEqualTo("해당하는 토픽명이 없습니다. 올바른 값을 입력해주세요.");
    }

    @Test
    @DisplayName("NOT_FOUND_AUTHOR_LEVEL 상태 확인")
    void notFoundAuthorLevel_상태확인() {
        // when
        ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL;

        // then
        assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(status.getCode()).isEqualTo("REFERENCE-002");
        assertThat(status.getMessage()).isEqualTo("해당 작성자 유형이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("NOT_FOUND_OCCUPATION 상태 확인")
    void notFoundOccupation_상태확인() {
        // when
        ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_OCCUPATION;

        // then
        assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(status.getCode()).isEqualTo("REFERENCE-003");
        assertThat(status.getMessage()).isEqualTo("해당 직업이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("NOT_FOUND_VISIT_SOURCE 상태 확인")
    void notFoundVisitSource_상태확인() {
        // when
        ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE;

        // then
        assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(status.getCode()).isEqualTo("REFERENCE-004");
        assertThat(status.getMessage()).isEqualTo("해당 방문 경로가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("NOT_FOUND_ANALYSIS_PURPOSE 상태 확인")
    void notFoundAnalysisPurpose_상태확인() {
        // when
        ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE;

        // then
        assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(status.getCode()).isEqualTo("REFERENCE-005");
        assertThat(status.getMessage()).isEqualTo("해당 분석 목적이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("NOT_FOUND_DATA_SOURCE 상태 확인")
    void notFoundDataSource_상태확인() {
        // when
        ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE;

        // then
        assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(status.getCode()).isEqualTo("REFERENCE-006");
        assertThat(status.getMessage()).isEqualTo("해당 데이터 출처가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("NOT_FOUND_DATA_TYPE 상태 확인")
    void notFoundDataType_상태확인() {
        // when
        ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_DATA_TYPE;

        // then
        assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(status.getCode()).isEqualTo("REFERENCE-007");
        assertThat(status.getMessage()).isEqualTo("해당 데이터 유형이 존재하지 않습니다.");
    }
}
