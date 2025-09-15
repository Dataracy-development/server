package com.dataracy.modules.email.domain.support;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.model.EmailContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class EmailContentFactoryTest {

    @ParameterizedTest(name = "[{index}] {0} → 제목/본문/코드 및 키워드 검증")
    @EnumSource(EmailVerificationType.class)
    @DisplayName("EmailVerificationType 별 EmailContent 생성 시 subject/body/코드 및 키워드를 검증한다")
    void generateShouldContainSubjectBodyCodeAndKeywords(EmailVerificationType type) {
        // given
        String code = "123456";

        // when
        EmailContent content = EmailContentFactory.generate(type, code);

        // then (공통 규칙)
        assertThat(content.subject())
                .as("제목은 비어있으면 안 된다")
                .isNotBlank();

        assertThat(content.body())
                .as("본문은 비어있으면 안 되고 코드 포함해야 한다")
                .isNotBlank()
                .contains(code);

        // then (타입별 규칙)
        switch (type) {
            case SIGN_UP -> {
                assertThat(content.subject()).contains("회원가입");
                assertThat(content.body()).contains("회원가입");
            }
            case PASSWORD_SEARCH -> {
                assertThat(content.subject()).contains("비밀번호 찾기");
                assertThat(content.body()).contains("비밀번호");
            }
            case PASSWORD_RESET -> {
                assertThat(content.subject()).contains("비밀번호 재설정");
                assertThat(content.body()).contains("재설정");
            }
        }
    }
}
