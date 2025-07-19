package com.dataracy.modules.user.application.service.query;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.user.application.dto.request.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.port.in.auth.HandleUserUseCase;
import com.dataracy.modules.user.application.port.in.auth.IsNewUserUseCase;
import com.dataracy.modules.user.application.port.in.user.ConfirmPasswordUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.user.IsLoginPossibleUseCase;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService implements
        IsNewUserUseCase,
        HandleUserUseCase,
        IsLoginPossibleUseCase,
        ConfirmPasswordUseCase,
        FindUsernameUseCase
{
    private final PasswordEncoder passwordEncoder;

    private final UserRepositoryPort userRepositoryPort;

    private final JwtValidateUseCase jwtValidateUseCase;
    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final TokenRedisUseCase tokenRedisUseCase;

    /**
     * 주어진 OAuth 제공자 ID로 사용자가 이미 존재하는지 확인하여 신규 사용자인지 반환합니다.
     *
     * @param oAuthUserInfo 소셜 인증에서 받은 사용자 정보
     * @return 사용자가 존재하지 않으면 true(신규 사용자), 존재하면 false
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isNewUser(OAuthUserInfo oAuthUserInfo) {
        return userRepositoryPort.findUserByProviderId(oAuthUserInfo.providerId()).isEmpty();
    }

    /**
     * 신규 사용자에 대해 등록 토큰을 생성하여 반환합니다.
     *
     * OAuth 제공자 정보로 등록용 JWT 토큰을 생성하고, 토큰과 만료 시간을 포함한 응답을 반환합니다.
     *
     * @param oAuthUserInfo OAuth2 인증을 통해 획득한 사용자 정보
     * @return 등록 토큰과 만료 시간이 포함된 RegisterTokenResponse 객체
     */
    @Override
    public RegisterTokenResponse handleNewUser(OAuthUserInfo oAuthUserInfo) {
        String registerToken = jwtGenerateUseCase.generateRegisterToken(
                oAuthUserInfo.provider(),
                oAuthUserInfo.providerId(),
                oAuthUserInfo.email()
        );
        log.info("신규 사용자 등록 토큰 생성 완료 - 이메일: {}", oAuthUserInfo.email());
        return new RegisterTokenResponse(registerToken, jwtValidateUseCase.getRegisterTokenExpirationTime());
    }

    /**
     * OAuth 제공자 ID로 기존 사용자를 조회하여 리프레시 토큰을 발급하고 Redis에 저장합니다.
     *
     * @param oAuthUserInfo OAuth2 사용자 정보
     * @return 발급된 리프레시 토큰과 만료 시간 정보를 담은 응답 객체
     */
    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResponse handleExistingUser(OAuthUserInfo oAuthUserInfo) {
        User existUser = userRepositoryPort.findUserByProviderId(oAuthUserInfo.providerId()).get();

        long refreshTokenExpirationTime = jwtValidateUseCase.getRefreshTokenExpirationTime();
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(existUser.getId(), existUser.getRole());

        // 리프레시 토큰 레디스 저장
        tokenRedisUseCase.saveRefreshToken(existUser.getId().toString(), refreshToken);

        log.info("기존 사용자 처리 완료: {}", existUser.getId());
        return new RefreshTokenResponse(refreshToken, refreshTokenExpirationTime);
    }

    /**
     * 이메일과 비밀번호로 사용자의 로그인 가능 여부를 확인하고, 성공 시 사용자 정보를 반환합니다.
     *
     * 사용자가 존재하지 않거나 비밀번호가 일치하지 않으면 `UserException`이 발생합니다.
     *
     * @param email 로그인에 사용되는 이메일 주소
     * @param password 로그인에 사용되는 비밀번호
     * @return 인증에 성공한 사용자의 정보
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfo isLogin(String email, String password) {
        User user = userRepositoryPort.findUserByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorStatus.BAD_REQUEST_LOGIN));

        if (!user.isPasswordMatch(passwordEncoder, password)) {
            throw new UserException(UserErrorStatus.BAD_REQUEST_LOGIN);
        }
        return new UserInfo(
                user.getId(),
                user.getRole(),
                user.getEmail(),
                user.getNickname(),
                user.getAuthorLevelId(),
                user.getOccupationId(),
                user.getTopicIds(),
                user.getVisitSourceId()
        );
    }

    /**
     * 주어진 사용자 ID와 비밀번호로 비밀번호가 정확한지 확인한다.
     *
     * 사용자가 존재하지 않거나 비밀번호가 일치하지 않으면 {@code UserException}이 발생한다.
     *
     * @param userId 비밀번호를 확인할 사용자 ID
     * @param requestDto 확인할 비밀번호가 포함된 요청 객체
     */
    @Override
    @Transactional(readOnly = true)
    public void confirmPassword(Long userId, ConfirmPasswordRequest requestDto) {
        User user = userRepositoryPort.findUserById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));

        boolean isMatched = user.isPasswordMatch(passwordEncoder, requestDto.password());
        if (!isMatched) {
            throw new UserException(UserErrorStatus.FAIL_CONFIRM_PASSWORD);
        }
    }

    /**
     * 주어진 사용자 ID로 사용자를 조회하여 닉네임을 반환합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자의 닉네임
     * @throws UserException 사용자를 찾을 수 없는 경우 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public String findUsernameById(Long userId) {
        User user = userRepositoryPort.findUserById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));
        return user.getNickname();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> findUsernamesByIds(List<Long> userIds) {
        return userRepositoryPort.findUsernamesByIds(userIds);
    }
}
