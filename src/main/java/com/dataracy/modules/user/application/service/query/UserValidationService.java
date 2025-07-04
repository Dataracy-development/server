package com.dataracy.modules.user.application.service.query;

import com.dataracy.modules.user.application.dto.request.DuplicateNicknameRequest;
import com.dataracy.modules.user.application.port.in.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.service.validator.UserDuplicateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 유효성 검사 중복 체크 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService implements DuplicateNicknameUseCase {

    private final UserDuplicateValidator userDuplicateValidator;

    /**
     * 닉네임 중복 확인.
     *
     * @param requestDto 닉네임 중복 확인 요청 정보
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedNickname(DuplicateNicknameRequest requestDto) {
        String nickname = requestDto.nickname();
        userDuplicateValidator.duplicateNickname(nickname);
        log.info("닉네임 사용 가능: {}", nickname);
    }
}
