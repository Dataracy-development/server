package com.dataracy.user.infra.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtProperties {
    private String secret;
    private long registerTokenExpirationTime;
    private long accessTokenExpirationTime;
    private long refreshTokenExpirationTime;
    private String redirectOnboarding;
    private String redirectBase;
}
