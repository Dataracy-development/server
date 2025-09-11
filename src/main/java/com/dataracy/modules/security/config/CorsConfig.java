package com.dataracy.modules.security.config;

import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${spring.profiles.active:local}")
    private String activeProfile;
    
    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;
    
    /**
     * 애플리케이션의 모든 경로에 대해 지정된 출처와 HTTP 메서드에 대한 CORS(Cross-Origin Resource Sharing) 정책을 적용하는 CorsConfigurationSource 빈을 생성합니다.
     * 환경에 따라 허용되는 Origin이 동적으로 설정됩니다.
     *
     * @return 모든 경로에 CORS 정책이 적용된 CorsConfigurationSource 인스턴스
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Referer",
                "User-Agent",
                "Access-Control-Allow-Credentials"
        ));

        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Set-Cookie",
                "Content-Disposition",
                "X-Total-Count"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * 환경에 따라 허용되는 Origin 목록을 반환합니다.
     */
    private List<String> getAllowedOrigins() {
        if ("prod".equals(activeProfile)) {
            // 운영 환경: HTTPS만 허용
            return List.of(
                    "https://dataracy.co.kr",
                    "https://www.dataracy.co.kr"
            );
        } else if ("dev".equals(activeProfile)) {
            // 개발 환경: HTTP/HTTPS 모두 허용
            return List.of(
                    "http://localhost:3000",
                    "http://localhost:8080",
                    "https://dev.api.dataracy.co.kr",
                    "http://dev.api.dataracy.co.kr",
                    "https://dataracy-client.vercel.app",
                    "http://dataracy.store",
                    "https://dataracy.store",
                    "http://dataracy.co.kr",
                    "https://dataracy.co.kr"
            );
        } else {
            // 로컬 환경: 모든 개발 도구 허용
            return List.of(
                    "http://localhost:3000",
                    "http://localhost:8080",
                    "http://localhost:63342",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:8080",
                    "http://127.0.0.1:63342"
            );
        }
    }
}
