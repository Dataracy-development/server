package com.dataracy.modules.security.config;

import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.Getter;

/** 보안 필터 예외 경로들을 관리하는 설정 클래스 */
@Component
@Getter
public class SecurityPathConfig {

  /** JWT 필터를 우회해야 하는 경로들 */
  private final Set<String> jwtExcludedPaths =
      Set.of(
          // Swagger 관련
          "/swagger",
          "/swagger-ui",
          "/v3/api-docs",
          "/swagger-resources",
          "/webjars",
          "/.well-known",
          "/swagger-ui.html",
          "/favicon.ico",

          // 헬스체크 및 모니터링
          "/health",
          "/actuator",

          // 정적 리소스
          "/static",
          "/webhook",

          // 공개 API
          "/api/v1/base",
          "/api/v1/onboarding",
          "/login",
          "/oauth2",
          "/",
          "/error",
          "/api/v1/references",
          "/api/v1/email",
          "/api/v1/signup",
          "/api/v1/auth",
          "/api/v1/users",
          "/api/v1/password/reset",
          "/api/v1/nickname/check",
          "/api/v1/files");

  /** GET 메서드에서 공개 허용되는 프로젝트 경로들 */
  private final Set<String> publicProjectPaths = Set.of("/api/v1/projects/");

  /** GET 메서드에서 공개 허용되는 데이터셋 경로들 */
  private final Set<String> publicDatasetPaths = Set.of("/api/v1/datasets/");

  /** 인증이 필요한 프로젝트 경로들 */
  private final Set<String> authenticatedProjectPaths =
      Set.of("/api/v1/projects/me", "/api/v1/projects/like");

  /** 인증이 필요한 데이터셋 경로들 */
  private final Set<String> authenticatedDatasetPaths = Set.of("/api/v1/datasets/me");

  /** 경로가 JWT 필터 예외 대상인지 확인 */
  public boolean isJwtExcludedPath(String path) {
    return jwtExcludedPaths.stream().anyMatch(path::startsWith);
  }

  /** 프로젝트 경로가 공개 허용되는지 확인 */
  public boolean isPublicProjectPath(String path) {
    return publicProjectPaths.stream().anyMatch(path::startsWith)
        && authenticatedProjectPaths.stream().noneMatch(path::startsWith);
  }

  /** 데이터셋 경로가 공개 허용되는지 확인 */
  public boolean isPublicDatasetPath(String path) {
    return publicDatasetPaths.stream().anyMatch(path::startsWith)
        && authenticatedDatasetPaths.stream().noneMatch(path::startsWith);
  }
}
