package com.dataracy.modules.common.config.adapter.persistence;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;

import com.dataracy.modules.security.handler.SecurityContextProvider;
import com.dataracy.modules.security.principal.CustomUserDetails;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
  /**
   * 현재 인증된 사용자의 ID를 반환하여 JPA 감사(Auditing) 기능에 사용합니다. 인증된 사용자가 없거나 익명 사용자일 경우 빈 Optional을 반환합니다.
   *
   * @return 현재 인증된 사용자의 ID(Long) 또는 사용자가 없을 경우 빈 Optional
   */
  @Bean
  public AuditorAware<Long> auditorProvider() {
    return () -> {
      Authentication authentication = SecurityContextProvider.getAuthentication();

      // 인증이 안되어 있거나 익명 사용자일 경우
      if (authentication == null
          || !authentication.isAuthenticated()
          || authentication.getPrincipal().equals("anonymousUser")) {
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
