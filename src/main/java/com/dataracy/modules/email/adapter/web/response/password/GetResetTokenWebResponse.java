package com.dataracy.modules.email.adapter.web.response.password;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비로그인 유저 비밀번호 재설정을 위한 웹 응답 DTO")
public record GetResetTokenWebResponse(
        @Schema(description = "비밀번호 재설정용 토큰", example = "ezsdfqef21e211rfd~~")
        String resetToken
) {}
