package com.dataracy.modules.security.handler;

import com.dataracy.modules.common.dto.response.ErrorResponse;
import com.dataracy.modules.common.status.BaseErrorCode;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.security.exception.SecurityException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @Mock
    private PrintWriter printWriter;

    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private ObjectMapper objectMapper;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        customAuthenticationEntryPoint = new CustomAuthenticationEntryPoint(objectMapper);
        stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
    }

    @Test
    @DisplayName("commence - BusinessException인 경우 해당 에러 코드로 응답한다")
    void commence_WhenBusinessException_ReturnsBusinessErrorResponse() throws IOException {
        // given
        BaseErrorCode errorCode = CommonErrorStatus.UNAUTHORIZED;
        SecurityException businessException = new SecurityException(errorCode);
        given(request.getAttribute("filter.error")).willReturn(businessException);

        // when
        customAuthenticationEntryPoint.commence(request, response, authException);

        // then
        then(response).should().setContentType("application/json");
        then(response).should().setStatus(errorCode.getHttpStatus().value());
        
        String responseBody = stringWriter.toString();
        ErrorResponse expectedResponse = ErrorResponse.of(errorCode);
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(responseBody).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("commence - BusinessException이 아닌 경우 500 에러로 응답한다")
    void commence_WhenNotBusinessException_ReturnsInternalServerError() throws IOException {
        // given
        RuntimeException runtimeException = new RuntimeException("Test exception");
        given(request.getAttribute("filter.error")).willReturn(runtimeException);

        // when
        customAuthenticationEntryPoint.commence(request, response, authException);

        // then
        then(response).should().setContentType("application/json");
        then(response).should().setStatus(500);
        
        String responseBody = stringWriter.toString();
        ErrorResponse expectedResponse = ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR);
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(responseBody).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("commence - filter.error가 null인 경우 500 에러로 응답한다")
    void commence_WhenFilterErrorIsNull_ReturnsInternalServerError() throws IOException {
        // given
        given(request.getAttribute("filter.error")).willReturn(null);

        // when
        customAuthenticationEntryPoint.commence(request, response, authException);

        // then
        then(response).should().setContentType("application/json");
        then(response).should().setStatus(500);
        
        String responseBody = stringWriter.toString();
        ErrorResponse expectedResponse = ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR);
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(responseBody).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("commence - IOException 발생 시 예외를 전파한다")
    void commence_WhenIOException_PropagatesException() throws IOException {
        // given
        given(request.getAttribute("filter.error")).willReturn(new SecurityException(CommonErrorStatus.UNAUTHORIZED));
        given(response.getWriter()).willThrow(new IOException("Writer error"));

        // when & then
        try {
            customAuthenticationEntryPoint.commence(request, response, authException);
        } catch (IOException e) {
            assertThat(e.getMessage()).isEqualTo("Writer error");
        }
    }
}
