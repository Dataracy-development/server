package com.dataracy.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * 통합 테스트를 위한 기본 클래스
 * 실제 서버를 띄우고 HTTP 요청을 통해 테스트를 수행합니다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    "logging.level.org.springframework.web=DEBUG"
})
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    protected TestRestTemplate restTemplate = new TestRestTemplate();

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    protected String getApiUrl(String path) {
        return getBaseUrl() + "/api/v1" + path;
    }
}
