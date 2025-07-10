//package com.dataracy.modules.common.config;
//
//import com.dataracy.modules.behaviorlog.adapter.filter.BehaviorLogMdcFilter;
//import jakarta.servlet.Filter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Bean;
//
//@Configuration
//@RequiredArgsConstructor
//public class FilterConfig {
//    private final BehaviorLogMdcFilter behaviorLogMdcFilter;
//
//    @Bean
//    public FilterRegistrationBean<Filter> behaviorLogFilter() {
//        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(behaviorLogMdcFilter);
//        registrationBean.setOrder(1); // 가장 먼저 실행되도록 우선순위 설정
//        registrationBean.addUrlPatterns("/*");
//        return registrationBean;
//    }
//}
