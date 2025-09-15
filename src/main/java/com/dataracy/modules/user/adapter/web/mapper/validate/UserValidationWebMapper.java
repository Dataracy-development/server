package com.dataracy.modules.user.adapter.web.mapper.validate;

import com.dataracy.modules.user.adapter.web.request.validate.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;
import org.springframework.stereotype.Component;

/**
 * 유저 유효성 검사 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class UserValidationWebMapper {
    /**
     * 닉네임 중복 체크 웹 요청 DTO를 애플리케이션 계층의 닉네임 중복 체크 요청 DTO로 변환합니다.
     *
     * @param webRequest 닉네임 중복 체크를 위한 웹 요청 DTO
     * @return 닉네임 정보를 담은 애플리케이션 계층의 닉네임 중복 체크 요청 DTO
     */
    public DuplicateNicknameRequest toApplicationDto(DuplicateNicknameWebRequest webRequest) {
        return new DuplicateNicknameRequest(
                webRequest.nickname()
        );
    }
}
