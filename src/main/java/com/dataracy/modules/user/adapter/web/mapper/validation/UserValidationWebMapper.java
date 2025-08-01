package com.dataracy.modules.user.adapter.web.mapper.validation;

import com.dataracy.modules.user.adapter.web.request.validation.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validation.DuplicateNicknameRequest;
import org.springframework.stereotype.Component;

@Component
public class UserValidationWebMapper {
    // 닉네임 중복 체크 웹 요청 DTO -> 닉네임 중복 체크 도메인 요청 DTO
    public DuplicateNicknameRequest toApplicationDto(DuplicateNicknameWebRequest webRequest) {
        return new DuplicateNicknameRequest(
                webRequest.nickname()
        );
    }
}
