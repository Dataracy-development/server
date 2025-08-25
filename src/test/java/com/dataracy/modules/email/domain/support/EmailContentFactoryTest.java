package com.dataracy.modules.email.domain.support;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.model.EmailContent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

class EmailContentFactoryTest {

    @ParameterizedTest(name = "목적 {0} 에 대해 제목/본문/코드 포함 검증")
    @EnumSource(EmailVerificationType.class)
    @DisplayName("모든 목적 타입에 대해 제목/본문이 비어있지 않고 코드가 포함된다")
    void generateAll(EmailVerificationType type) {
        // given
        String code = "123456";

        // when
        EmailContent content = EmailContentFactory.generate(type, code);

        // then
        assertThat(content.subject()).isNotBlank();
        assertThat(content.body()).isNotBlank();
        assertThat(content.body()).contains(code);
    }
}
