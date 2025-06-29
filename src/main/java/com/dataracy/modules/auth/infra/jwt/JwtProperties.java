package com.dataracy.modules.auth.infra.jwt;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtProperties {

    @Min(value = 1, message = "레지스터 토큰 유효시간은 1 이상이어야 합니다.")
    private long registerTokenExpirationTime;

    @Min(value = 1, message = "어세스 토큰 유효시간은 1 이상이어야 합니다.")
    private long accessTokenExpirationTime;

    @Min(value = 1, message = "리프레시 토큰 유효시간은 1 이상이어야 합니다.")
    private long refreshTokenExpirationTime;

    @NotBlank(message = "신규유저의 리다이렉트 주소를 작성해주세요.")
    private String redirectOnboarding;

    @NotBlank(message = "기존유저의 리다이렉트 주소를 작성해주세요.")
    private String redirectBase;
}
