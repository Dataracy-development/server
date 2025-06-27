package com.dataracy.modules.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            // 인증되지 않은 사용자 처리 (익명 사용자 또는 비로그인)
//            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//                return Optional.of("anonymous");
//            }
//
//            // 실무 기준: 유저 ID (Long)을 문자열로 반환
//            Object principal = authentication.getPrincipal();
//
//            if (principal instanceof CustomUserDetails userDetails) {
//                return Optional.of(String.valueOf(userDetails.getUserId()));
//            }
//            //예외 상황 대응
//            return Optional.of("unknown");
//        };
//    }
}
