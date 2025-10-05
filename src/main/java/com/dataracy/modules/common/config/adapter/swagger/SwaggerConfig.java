package com.dataracy.modules.common.config.adapter.swagger;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dataracy.modules.common.config.properties.SwaggerProperties;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

/** 스웨거 문서화를 위한 설정 http(s)://<서버주소>:<포트번호>/swagger-ui/index.html로 접속 */
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
  private final SwaggerProperties swaggerProperties;

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  @Bean
  public OpenAPI publicApi() {
    Info info =
        new Info()
            .title(swaggerProperties.getTitle())
            .description(swaggerProperties.getDescription())
            .version(swaggerProperties.getVersion());

    SecurityScheme securityScheme =
        new SecurityScheme()
            .name("Authorization")
            .type(SecurityScheme.Type.HTTP)
            .in(HEADER)
            .scheme("Bearer")
            .bearerFormat("JWT");

    Components components = new Components().addSecuritySchemes("bearerAuth", securityScheme);

    OpenAPI openAPI =
        new OpenAPI()
            .info(info)
            .components(components)
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

    // 환경에 따라 서버 정보 추가
    addServerInfo(openAPI);

    return openAPI;
  }

  /** 환경에 따라 서버 정보를 추가합니다. */
  private void addServerInfo(OpenAPI openAPI) {
    if ("prod".equals(activeProfile)) {
      // 운영 환경: HTTPS만
      openAPI.addServersItem(
          new Server()
              .url("https://api.dataracy.co.kr")
              .description("Production Server (HTTPS Only)"));
    } else if ("dev".equals(activeProfile)) {
      // 개발 환경: HTTP만 사용
      openAPI.addServersItem(
          new Server()
              .url("http://dev-api.dataracy.co.kr:8080")
              .description("Development Server (HTTP Only)"));
    } else {
      // 로컬 환경: HTTP만
      openAPI.addServersItem(
          new Server().url("http://localhost:8080").description("Local Development Server (HTTP)"));
    }
  }
}
