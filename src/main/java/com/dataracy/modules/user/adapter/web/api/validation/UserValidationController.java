package com.dataracy.modules.user.adapter.web.api.validation;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.validation.UserValidationWebMapper;
import com.dataracy.modules.user.adapter.web.request.validation.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validation.DuplicateNicknameRequest;
import com.dataracy.modules.user.application.port.in.validation.DuplicateNicknameUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class UserValidationController implements UserValidationApi {
    private final UserValidationWebMapper userWebMapper;

    private final DuplicateNicknameUseCase duplicateNicknameUseCase;

    /**
     * 닉네임의 중복 여부를 검사하여 중복이 아닐 경우 성공 응답을 반환한다.
     *
     * @param webRequest 중복 검사를 요청하는 닉네임 정보가 포함된 객체
     * @return 닉네임이 중복되지 않을 경우 성공 상태를 담은 HTTP 200 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> duplicateNickname(
            DuplicateNicknameWebRequest webRequest
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[DuplicateNickname] 닉네임 중복 API 요청 시작");
        DuplicateNicknameRequest requestDto = userWebMapper.toApplicationDto(webRequest);
        duplicateNicknameUseCase.validateDuplicatedNickname(requestDto.nickname());
        LoggerFactory.api().logResponse("[DuplicateNickname] 닉네임 중복 API 응답 완료", startTime);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME));
    }

    /**
     * "/onboarding" 경로에 대한 GET 요청을 처리하여 "onboarding" 뷰 이름을 반환합니다.
     *
     * @return "onboarding" 뷰 이름
     */
    @GetMapping("/onboarding")
    public String onboarding(Model model) {
        return "onboarding";
    }

    /**
     * "/base" 경로에 대한 GET 요청을 처리하여 "base" 뷰 이름을 반환합니다.
     *
     * @return "base" 뷰 이름
     */
    @GetMapping("/base")
    public String base(Model model) {
        return "base";
    }
}
