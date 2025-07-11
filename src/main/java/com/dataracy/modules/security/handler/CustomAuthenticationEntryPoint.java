package com.dataracy.modules.security.handler;

import com.dataracy.modules.common.dto.response.ErrorResponse;
import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 서비스와 관련된 에러사항은 ControllerAdvice에서 처리할 수 있지만
 * securityContext내의 에러사항은 시큐리티의 exceptionHandling에서 처리하기 때문에
 * CustomAuthenticationEntryPoint를 통해 에러처리를 진행한다.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // security context 과정에서 일어난 에러르 받아 직접 처리해준다.
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object ex = request.getAttribute("filter.error");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (ex instanceof BusinessException businessEx) {
            BaseErrorCode errorCode = businessEx.getErrorCode();
            ErrorResponse errorResponse = ErrorResponse.of(errorCode);
            response.setStatus(errorResponse.getHttpStatus());
            objectMapper.writeValue(response.getWriter(), errorResponse);
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ErrorResponse errorResponse = ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }
}
