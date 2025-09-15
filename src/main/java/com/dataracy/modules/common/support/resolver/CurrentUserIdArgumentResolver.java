package com.dataracy.modules.common.support.resolver;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * CurrentUserId 어노테이션에 바로 값을 주입받을 수 있게 설정한다.
 */
@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * ArgumentResolver를 통해 파라미터에 있는 CurrentUserId 애노테이션의 유효성을 확인한다.
     *
     * @param parameter 메서드 파라미터
     * @return 파라미터에 존재 여부
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserId.class) != null
                && parameter.getParameterType().equals(Long.class);
    }

    /**
     * Arguemnt Resolver에서 현재 인증된 객체의 유저 아이디를 반환한다.
     *
     * @param parameter 메서드 파라미터
     * @param mavContainer 모델 앤 뷰 컨테이너
     * @param webRequest 웹 요청 객체
     * @param binderFactory 웹 바인더
     * @return
     */
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
