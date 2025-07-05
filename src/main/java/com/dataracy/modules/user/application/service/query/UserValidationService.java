package com.dataracy.modules.user.application.service.query;

import com.dataracy.modules.user.application.port.in.user.DuplicateEmailUseCase;
import com.dataracy.modules.user.application.port.in.user.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.service.validator.UserDuplicateValidator;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
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
public class UserValidationService implements DuplicateNicknameUseCase, DuplicateEmailUseCase {
    private final UserDuplicateValidator userDuplicateValidator;

    /**
     * 닉네임 중복 확인.
     *
     * @param nickname 닉네임
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedNickname(String nickname) {
        userDuplicateValidator.duplicateNickname(nickname);
        log.info("닉네임 사용 가능: {}", nickname);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedEmail(String email) {
        User duplicatedUser = userDuplicateValidator.duplicateEmail(email);
        if (duplicatedUser != null) {
            ProviderType providerType = duplicatedUser.getProvider();
            switch (providerType) {
                case GOOGLE -> throw new UserException(UserErrorStatus.DUPLICATED_GOOGLE_EMAIL);
                case KAKAO -> throw new UserException(UserErrorStatus.DUPLICATED_KAKAO_EMAIL);
                case LOCAL -> throw new UserException(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
            }
        }
    }
}
