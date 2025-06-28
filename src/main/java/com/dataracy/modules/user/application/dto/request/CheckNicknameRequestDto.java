package com.dataracy.modules.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckNicknameRequestDto (

        @NotBlank(message = "닉네임은 공백 또는 null값일 수 없습니다.")
        @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
        String nickname

) {
}
