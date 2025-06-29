package com.dataracy.modules.common.config;

import com.dataracy.modules.common.properties.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

/**
 * 스웨거 문서화를 위한 설정
 * http(s)://<서버주소>:<포트번호>/swagger-ui/index.html로 접속
 */
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI publicApi() {
        Info info = new Info()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion());

        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .in(HEADER)
                .scheme("Bearer")
                .bearerFormat("JWT");

        Components components = new Components().addSecuritySchemes("bearerAuth", securityScheme);

        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .addServersItem(new Server().url(swaggerProperties.getServerUrl()).description(swaggerProperties.getServerDescription()));
    }
}
