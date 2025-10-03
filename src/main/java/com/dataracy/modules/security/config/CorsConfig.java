/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.security.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
  /**
   * 애플리케이션의 모든 경로에 대해 지정된 출처와 HTTP 메서드에 대한 CORS(Cross-Origin Resource Sharing) 정책을 적용하는
   * CorsConfigurationSource 빈을 생성합니다. 프론트엔드 개발 환경, Swagger UI, 개발 및 운영 서버 등 특정 출처에서의 요청만 허용하며, 모든
   * 헤더와 인증 정보를 포함한 요청을 지원합니다. 사전 요청(preflight)의 최대 유효 시간은 1시간(3600초)입니다.
   *
   * @re 경로에 CORS 정책이 적용된 CorsConfigurationSource 인스턴스
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of(
            "http://localhost:3000", // 프론트엔드 개발 주소
            "http://localhost:8080", // Swagger UI (같은 백엔드 서버지만, 브라우저에서 열기 때문에 Origin 임)
            "http://dataracy.co.kr",
            "https://dataracy.co.kr",
            "http://www.dataracy.co.kr",
            "https://www.dataracy.co.kr",
            "https://api.dataracy.co.kr",
            "http://dev-api.dataracy.co.kr:8080",
            "https://dev-api.dataracy.co.kr:8443",
            "http://localhost:63342",
            "https://dataracy-client.vercel.app/"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    configuration.setAllowedHeaders(
        Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Referer",
            "User-Agent",
            "Access-Control-Allow-Credentials"));

    configuration.setExposedHeaders(
        Arrays.asList("Authorization", "Set-Cookie", "Content-Disposition", "X-Total-Count"));

    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
