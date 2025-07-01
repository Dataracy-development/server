package com.dataracy.modules.email.domain.model;

import lombok.*;

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

