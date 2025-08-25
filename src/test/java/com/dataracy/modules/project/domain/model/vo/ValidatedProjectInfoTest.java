package com.dataracy.modules.project.domain.model.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ValidatedProjectInfoTest {

    @Test
    @DisplayName("record 생성 및 값 검증")
    void validatedProjectInfo_success() {
        // given
        ValidatedProjectInfo info = new ValidatedProjectInfo("t", "ap", "ds", "al");

        // then
        assertThat(info.topicLabel()).isEqualTo("t");
        assertThat(info.analysisPurposeLabel()).isEqualTo("ap");
        assertThat(info.dataSourceLabel()).isEqualTo("ds");
        assertThat(info.authorLevelLabel()).isEqualTo("al");
    }
}
