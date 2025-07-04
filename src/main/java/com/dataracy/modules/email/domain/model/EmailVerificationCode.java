package com.dataracy.modules.email.domain.model;

import lombok.*;

/**
 * 이메일 인증 도메인 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EmailVerificationCode {

    private String email;
    private String code;

    public static EmailVerificationCode toDomain(
            String email,
            String code
    ) {
        return EmailVerificationCode.builder()
                .email(email)
                .code(code)
                .build();
    }
}
