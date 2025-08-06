package com.dataracy.modules.security.config;

import com.dataracy.modules.auth.adapter.handler.OAuth2LoginFailureHandler;
import com.dataracy.modules.auth.adapter.handler.OAuth2LoginSuccessHandler;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.security.filter.JwtFilter;
import com.dataracy.modules.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
    private final CorsConfigurationSource corsConfigurationSource;

    private final JwtValidateUseCase jwtValidateUseCase;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * Spring Security의 보안 필터 체인을 설정하여 HTTP 요청에 대한 인증 및 인가 정책을 적용합니다.
     *
     * CORS 활성화, CSRF 및 기본 인증 방식 비활성화, 세션 무상태화, OAuth2 로그인 처리, JWT 인증 필터 추가, 엔드포인트별 접근 권한 제어를 포함합니다.
     * 인증 없이 접근 가능한 엔드포인트(예: Swagger, 정적 리소스, 회원가입, 프로젝트/데이터셋/파일 조회 등)와 사용자 또는 관리자 권한이 필요한 엔드포인트를 구분하여 접근을 제한합니다.
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
                                "/health", "/actuator/**",
                                "/static/**", "/webhook").permitAll()
                        .requestMatchers("/api/v1/base", "/api/v1/onboarding").permitAll()
                        .requestMatchers("/login/**", "/login/oauth2/**", "/oauth2/**").permitAll()
                        .requestMatchers("/", "/api/v1/nickname/check", "/api/v1/signup/**").permitAll()
                        .requestMatchers("/api/v1/password/reset").permitAll()
                        .requestMatchers("/api/v1/references/**").permitAll()
                        .requestMatchers("/api/v1/email/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/datasets/**").permitAll()
                        .requestMatchers("/api/v1/files/**").permitAll()
                        .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtValidateUseCase), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * actuator 요청은 security filter 자체에서 완전히 제외시킴 (필터 자체 적용 안 됨)
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/actuator/**");
    }
}
