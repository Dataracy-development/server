package com.dataracy.modules.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * 지정된 출처(origin)와 HTTP 메서드에 대해 CORS(Cross-Origin Resource Sharing) 정책을 설정하는 CorsConfigurationSource 빈을 생성합니다.
     *
     * 프론트엔드 개발, Swagger UI, 개발 및 운영 서버에서의 요청만 허용하며, 모든 헤더와 인증 정보를 포함한 요청을 지원합니다.
     * 사전 요청(preflight)의 최대 유효 시간은 1시간(3600초)입니다.
     *
     * @return 모든 경로에 대해 CORS 정책이 적용된 CorsConfigurationSource 인스턴스
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",   // 프론트엔드 개발 주소
                "http://localhost:8080",   // Swagger UI (같은 백엔드 서버지만, 브라우저에서 열기 때문에 Origin 임)
                "http://dataracy.co.kr:8083", // 개발용 서버
                "http://dataracy.co.kr", // 운영용 서버
                "http://localhost:63342"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
