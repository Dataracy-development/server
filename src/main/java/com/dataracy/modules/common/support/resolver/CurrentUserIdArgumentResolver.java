package com.dataracy.modules.common.support.resolver;

import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserId.class) != null
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        try {
            return SecurityContextProvider.getAuthenticatedUserId();
        } catch (Exception e) {
            throw new CommonException(CommonErrorStatus.UNAUTHORIZED);
        }
    }
}
