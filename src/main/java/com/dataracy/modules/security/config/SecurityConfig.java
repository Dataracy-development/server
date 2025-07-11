package com.dataracy.modules.security.config;

import com.dataracy.modules.auth.adapter.handler.OAuth2LoginFailureHandler;
import com.dataracy.modules.auth.adapter.handler.OAuth2LoginSuccessHandler;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.security.filter.JwtFilter;
import com.dataracy.modules.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 스프링 시큐리티 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtValidateUseCase jwtValidateUseCase;
    private final CorsConfigurationSource corsConfigurationSource;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * Spring Security의 HTTP 요청 보안 필터 체인을 구성합니다.
     *
     * CORS, CSRF, 세션 관리, 예외 처리, OAuth2 로그인, JWT 인증, 그리고 엔드포인트별 접근 권한을 설정합니다.
     * 인증이 필요 없는 공개 엔드포인트와 사용자/관리자 권한이 필요한 엔드포인트를 구분하여 접근을 제어합니다.
     *
     * @return 구성된 SecurityFilterChain 인스턴스
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/swagger-config/**",
                                "/webjars/**", "/.well-known/**", "/favicon.ico",
                                "/static/**", "/webhook").permitAll()
                        .requestMatchers("/api/v1/base", "/api/v1/onboarding").permitAll()
                        .requestMatchers("/login/**", "/login/oauth2/**", "/oauth2/**", "/moniter/**").permitAll()
                        .requestMatchers("/", "/api/v1/nickname/check", "/api/v1/signup/**").permitAll()
                        .requestMatchers("/api/v1/topics", "/api/v1/author-levels", "/api/v1/occupations", "/api/v1/visit-sources").permitAll()
                        .requestMatchers("/api/v1/email/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtValidateUseCase), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
