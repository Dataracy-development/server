package com.dataracy.modules.email.adapter.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
class SendEmailSendGridAdapterTest {

    @Mock
    SendGrid client;

    @InjectMocks
    SendEmailSendGridAdapter adapter;

    @BeforeEach
    void init() {
        // 필수 프로퍼티 세팅
        ReflectionTestUtils.setField(adapter, "sender", "no-reply@dataracy.co.kr");
    }

    @Nested
    @DisplayName("send")
    class Send {

        @Test
        @DisplayName("성공: 202 응답이면 예외 없이 종료, 요청은 POST /mail/send")
        void success() throws Exception {
            // given
            Response ok = new Response();
            ok.setStatusCode(202);
            given(client.api(any(Request.class))).willReturn(ok);

            // when
            adapter.send("to@example.com", "title", "body");

            // then
            then(client).should().api(argThat(req ->
                    req != null
                            && req.getMethod() == Method.POST
                            && "mail/send".equals(req.getEndpoint())
            ));
            assertThatCode(() -> {}).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 202가 아닌 상태코드면 RuntimeException")
        void failNon202() throws Exception {
            // given
            Response fail = new Response();
            fail.setStatusCode(500);
            given(client.api(any(Request.class))).willReturn(fail);

            // when
            RuntimeException ex = catchThrowableOfType(
                    () -> adapter.send("to@example.com", "title", "body"),
                    RuntimeException.class
            );

            // then
            assertThat(ex).isNotNull();
        }

        @Test
        @DisplayName("실패: IOException → RuntimeException wrap")
        void ioException() throws Exception {
            // given
            willAnswer(inv -> { throw new IOException("io"); })
                    .given(client).api(any(Request.class));

            // when
            RuntimeException ex = catchThrowableOfType(
                    () -> adapter.send("to@example.com", "title", "body"),
                    RuntimeException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getCause()).isInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("@PostConstruct validateProperties")
    class ValidateProperties {

        @Test
        @DisplayName("sender가 비어있으면 IllegalStateException")
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
    }
}
