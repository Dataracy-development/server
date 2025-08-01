package com.dataracy.modules.user.adapter.web.mapper.password;

import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordWebMapper {
    /**
     * 비밀번호 변경 웹 요청 DTO를 애플리케이션 계층의 비밀번호 변경 요청 DTO로 변환합니다.
     *
     * @param webRequest 비밀번호와 비밀번호 확인 값을 포함하는 웹 요청 DTO
     * @return 비밀번호와 비밀번호 확인 값을 담은 비밀번호 변경 요청 DTO
     */
    public ChangePasswordRequest toApplicationDto(ChangePasswordWebRequest webRequest) {
        return new ChangePasswordRequest(
                webRequest.password(),
                webRequest.passwordConfirm()
        );
    }

    /**
     * 비밀번호 확인 웹 요청 DTO를 비밀번호 확인 도메인 요청 DTO로 변환합니다.
     *
     * @param webRequest 비밀번호 확인 정보를 담고 있는 웹 요청 DTO
     * @return 변환된 비밀번호 확인 도메인 요청 DTO
     */
    public ConfirmPasswordRequest toApplicationDto(ConfirmPasswordWebRequest webRequest) {
        return new ConfirmPasswordRequest(
                webRequest.password()
        );
    }
}
