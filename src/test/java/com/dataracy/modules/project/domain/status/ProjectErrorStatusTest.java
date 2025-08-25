package com.dataracy.modules.project.domain.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProjectErrorStatusTest {

    @Test
    @DisplayName("에러 코드 필드 값 검증")
    void errorStatus_fields() {
        ProjectErrorStatus status = ProjectErrorStatus.NOT_FOUND_PROJECT;

        assertThat(status.getHttpStatus().value()).isEqualTo(404);
        assertThat(status.getCode()).isEqualTo("PROJECT-002");
        assertThat(status.getMessage()).contains("존재하지 않습니다");
    }
}

