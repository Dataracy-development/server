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

import java.util.Optional;

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

    /**
     * 주어진 이메일이 이미 등록되어 있는지 검증한다.
     *
     * 이메일이 중복된 경우, 해당 이메일의 가입 경로(구글, 카카오, 로컬)에 따라 각각의 중복 예외를 발생시킨다.
     *
     * @param email 중복 여부를 확인할 이메일 주소
     * @throws UserException 이메일이 이미 등록된 경우, 가입 경로에 따라 다른 에러 상태로 예외가 발생함
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedEmail(String email) {
        Optional<User> duplicatedUser = userDuplicateValidator.duplicateEmail(email);
        if (duplicatedUser.isPresent()) {
            ProviderType providerType = duplicatedUser.get().getProvider();
            switch (providerType) {
                case GOOGLE -> throw new UserException(UserErrorStatus.DUPLICATED_GOOGLE_EMAIL);
                case KAKAO -> throw new UserException(UserErrorStatus.DUPLICATED_KAKAO_EMAIL);
                case LOCAL -> throw new UserException(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
            }
        }
    }
}
