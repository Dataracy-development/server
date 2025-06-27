package com.dataracy.modules.auth.infra.jwt;

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

    @NotBlank(message = "암호화를 위한 jwt 시크릿 키를 입력하세요.")
    private String secret;

    @NotBlank(message = "레지스터토큰 유효시간을 작성해주세요.")
    private long registerTokenExpirationTime;

    @NotBlank(message = "어세스토큰 유효시간을 작성해주세요.")
    private long accessTokenExpirationTime;

    @NotBlank(message = "리프레시토큰 유효시간을 작성해주세요.")
    private long refreshTokenExpirationTime;

    @NotBlank(message = "신규유저의 리다이렉트 주소를 작성해주세요.")
    private String redirectOnboarding;

    @NotBlank(message = "기존유저의 리다이렉트 주소를 작성해주세요.")
    private String redirectBase;
}
