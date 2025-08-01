package com.dataracy.modules.user.adapter.web.mapper.validation;

import com.dataracy.modules.user.adapter.web.request.validation.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validation.DuplicateNicknameRequest;
import org.springframework.stereotype.Component;

@Component
public class UserValidationWebMapper {
    /**
     * 닉네임 중복 체크 웹 요청 DTO를 도메인 계층의 닉네임 중복 체크 요청 DTO로 변환합니다.
     *
     * @param webRequest 닉네임 중복 체크를 위한 웹 요청 DTO
     * @return 닉네임 정보를 담은 도메인 계층의 닉네임 중복 체크 요청 DTO
     */
    public DuplicateNicknameRequest toApplicationDto(DuplicateNicknameWebRequest webRequest) {
        return new DuplicateNicknameRequest(
                webRequest.nickname()
        );
    }
}
