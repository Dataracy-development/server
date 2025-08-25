package com.dataracy.modules.email.adapter.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SendEmailSesAdapterTest {

    AmazonSimpleEmailService client;
    SendEmailSesAdapter adapter;

    @BeforeEach
    void setUp() {
        // given
        client = mock(AmazonSimpleEmailService.class);
        adapter = new SendEmailSesAdapter(client);
        ReflectionTestUtils.setField(adapter, "sender", "no-reply@dataracy.co.kr");
    }

    @Test
    @DisplayName("validateProperties - sender가 비어있으면 IllegalStateException")
    void blankSender() {
        // given
        ReflectionTestUtils.setField(adapter, "sender", "");

        // when
        IllegalStateException ex = catchThrowableOfType(
                () -> adapter.validateProperties(),
                IllegalStateException.class
        );

        // then
        assertThat(ex).isNotNull();
    }

    @Test
    @DisplayName("validateProperties - sender가 세팅되어 있으면 예외 없이 통과")
    void validSender() {
        // given (setUp에서 sender 설정 완료)

        // when & then
        assertThatCode(() -> adapter.validateProperties()).doesNotThrowAnyException();
    }
}
