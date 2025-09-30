package com.dataracy.modules.user.application.service.query.validate;

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

    // Use Case 상수 정의
    private static final String DUPLICATE_NICKNAME_USE_CASE = "DuplicateNicknameUseCase";
    private static final String DUPLICATE_EMAIL_USE_CASE = "DuplicateEmailUseCase";
    private static final String DUPLICATE_EMAIL_MESSAGE = "[중복 이메일 검증] 이메일 ";

    /**
     * 주어진 닉네임이 이미 사용 중인지 검증합니다.
     *
     * @param nickname 중복 여부를 확인할 닉네임
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedNickname(String nickname) {
        Instant startTime = LoggerFactory.service().logStart(DUPLICATE_NICKNAME_USE_CASE, "닉네임 중복 여부 확인 서비스 시작 nickname: " + nickname);
        userDuplicateValidator.duplicateNickname(nickname);
        LoggerFactory.service().logSuccess(DUPLICATE_NICKNAME_USE_CASE, "닉네임 중복 여부 확인 서비스 성공 nickname=" + nickname, startTime);
    }

    /**
     * 주어진 이메일의 중복 여부를 검사하고, 이미 등록된 경우 가입 경로에 따라 적절한 UserException을 던진다.
     *
     * 중복 사용자가 존재하면 해당 사용자의 ProviderType에 따라 다음과 같은 UserErrorStatus로 예외가 발생한다:
     * - GOOGLE  -> DUPLICATED_GOOGLE_EMAIL
     * - KAKAO   -> DUPLICATED_KAKAO_EMAIL
     * - LOCAL   -> DUPLICATED_LOCAL_EMAIL
     * - null 또는 그 외의 값 -> DUPLICATED_LOCAL_EMAIL
     *
     * 중복 사용자가 없으면 정상적으로 반환한다(예외 없음).
     *
     * @param email 중복 여부를 확인할 이메일 주소
     * @throws UserException 이메일이 이미 등록되어 있을 때, 위에 명시된 ProviderType별 중복 이메일 상태로 발생함
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDuplicatedEmail(String email) {
        Instant startTime = LoggerFactory.service().logStart(DUPLICATE_EMAIL_USE_CASE, "이메일 중복 여부 확인 서비스 시작 email=" + email);
        userDuplicateValidator.duplicateEmail(email)
                .ifPresent(user -> {
                    ProviderType providerType = user.getProvider();
                    if (providerType == null) {
                        LoggerFactory.service().logWarning(DUPLICATE_EMAIL_USE_CASE,
                                DUPLICATE_EMAIL_MESSAGE + email + "은 ProviderType이 null입니다.");
                        throw new UserException(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
                    }
                    switch (providerType) {
                        case GOOGLE -> {
                            LoggerFactory.service().logWarning(DUPLICATE_EMAIL_USE_CASE, DUPLICATE_EMAIL_MESSAGE + email + "은 구글 소셜 로그인으로 가입된 계정입니다.");
                            throw new UserException(UserErrorStatus.DUPLICATED_GOOGLE_EMAIL);
                        }
                        case KAKAO -> {
                            LoggerFactory.service().logWarning(DUPLICATE_EMAIL_USE_CASE, DUPLICATE_EMAIL_MESSAGE + email + "은 카카오 소셜 로그인으로 가입된 계정입니다.");
                            throw new UserException(UserErrorStatus.DUPLICATED_KAKAO_EMAIL);
                        }
                        case LOCAL -> {
                            LoggerFactory.service().logWarning(DUPLICATE_EMAIL_USE_CASE, DUPLICATE_EMAIL_MESSAGE + email + "은 자체 로그인으로 가입된 계정입니다.");
                            throw new UserException(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
                        }
                        default -> {
                            LoggerFactory.service().logWarning(
                                    DUPLICATE_EMAIL_USE_CASE,
                                    DUPLICATE_EMAIL_MESSAGE + email + "은 알 수 없는 ProviderType(" + providerType + ")으로 가입된 계정입니다.");
                            throw new UserException(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
                        }
                    }
                });
        LoggerFactory.service().logSuccess(DUPLICATE_EMAIL_USE_CASE, "이메일 중복 여부 확인 서비스 성공 email=" + email, startTime);
    }
}
