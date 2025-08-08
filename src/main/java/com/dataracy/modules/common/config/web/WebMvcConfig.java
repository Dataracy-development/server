package com.dataracy.modules.common.config.web;

import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final MultipartJackson2HttpMessageConverter multipartJackson2HttpMessageConverter;
    private final CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    /**
     * 가장 앞에 추가해 Jackson의 default converter보다 우선되게 하여
     * multipart/form-data에서의 http message converter에서의 문제를 해결한다.
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, multipartJackson2HttpMessageConverter);
    }

    /**
     * 인증 객체에서 CurrentUserId로의 유저 id 자동주입을 위한 리졸버를 추가한다.
     * @param resolvers 리졸버 리스트
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdArgumentResolver);
    }
}
