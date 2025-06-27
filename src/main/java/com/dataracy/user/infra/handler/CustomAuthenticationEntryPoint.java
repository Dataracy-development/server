package com.dataracy.user.infra.handler;

import com.dataracy.common.dto.ErrorResponse;
import com.dataracy.common.exception.BusinessException;
import com.dataracy.common.status.BaseErrorCode;
import com.dataracy.common.status.CommonErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object ex = request.getAttribute("filter.error");

        if (ex instanceof BusinessException businessEx) {
            BaseErrorCode errorCode = businessEx.getErrorCode();
            ErrorResponse errorResponse = ErrorResponse.of(errorCode);
            response.setStatus(errorResponse.getHttpStatus());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), errorResponse);
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ErrorResponse errorResponse = ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }
}
