package com.dataracy.modules.user.application.service.query.validation;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.port.in.validate.DuplicateEmailUseCase;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.service.validate.UserDuplicateValidator;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ValidateUserService implements DuplicateNicknameUseCase, DuplicateEmailUseCase {
    private final UserDuplicateValidator userDuplicateValidator;

    /**
     * 주어진 닉네임이 이미 사용 중인지 검증합니다.
     *
     * @param nickname 중복 여부를 확인할 닉네임
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedNickname(String nickname) {
        Instant startTime = LoggerFactory.service().logStart("DuplicateNicknameUseCase", "닉네임 중복 여부 확인 서비스 시작 nickname: " + nickname);
        userDuplicateValidator.duplicateNickname(nickname);
        LoggerFactory.service().logSuccess("DuplicateNicknameUseCase", "닉네임 중복 여부 확인 서비스 성공 nickname=" + nickname, startTime);
    }

    /**
     * 주어진 이메일이 이미 등록되어 있는지 검증한다.
     *
     * 이메일이 이미 등록된 경우, 해당 이메일의 가입 경로(구글, 카카오, 로컬)에 따라 각각 다른 중복 이메일 에러 상태의 UserException을 발생시킨다.
     *
     * @param email 중복 여부를 확인할 이메일 주소
     * @throws UserException 이메일이 이미 등록되어 있을 때, 가입 경로에 따라 각각의 중복 이메일 에러 상태로 예외가 발생함
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedEmail(String email) {
        Instant startTime = LoggerFactory.service().logStart("DuplicateEmailUseCase", "이메일 중복 여부 확인 서비스 시작 email=" + email);
        userDuplicateValidator.duplicateEmail(email)
                .ifPresent(user -> {
                    ProviderType providerType = user.getProvider();
                    switch (providerType) {
                        case GOOGLE -> {
                            LoggerFactory.service().logWarning("DuplicateEmailUseCase", "[중복 이메일 검증] 이메일 " + email + "은 구글 소셜 로그인으로 가입된 계정입니다.");
                            throw new UserException(UserErrorStatus.DUPLICATED_GOOGLE_EMAIL);
                        }
                        case KAKAO -> {
                            LoggerFactory.service().logWarning("DuplicateEmailUseCase", "[중복 이메일 검증] 이메일 " + email + "은 카카오 소셜 로그인으로 가입된 계정입니다.");
                            throw new UserException(UserErrorStatus.DUPLICATED_KAKAO_EMAIL);
                        }
                        case LOCAL -> {
                            LoggerFactory.service().logWarning("DuplicateEmailUseCase", "[중복 이메일 검증] 이메일 " + email + "은 자체 로그인으로 가입된 계정입니다.");
                            throw new UserException(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
                        }
                    }
                });
        LoggerFactory.service().logSuccess("DuplicateEmailUseCase", "이메일 중복 여부 확인 서비스 성공 email=" + email, startTime);
    }
}
