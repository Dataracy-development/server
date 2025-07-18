package com.dataracy.modules.common.config;

import com.dataracy.modules.security.handler.SecurityContextProvider;
import com.dataracy.modules.security.principal.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * 작성자, 수정자 자동 주입
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {

            Authentication authentication = SecurityContextProvider.getAuthentication();

            // 인증이 안되어 있거나 익명 사용자일 경우
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails userDetails) {
                return Optional.of(userDetails.getUserId());
            }

            return Optional.empty();
        };
    }
}
