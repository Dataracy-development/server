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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService implements IsNewUserUseCase, HandleUserUseCase, IsLoginPossibleUseCase, ConfirmPasswordUseCase {
    private final PasswordEncoder passwordEncoder;

    private final UserRepositoryPort userRepositoryPort;

    private final JwtValidateUseCase jwtValidateUseCase;
    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final TokenRedisUseCase tokenRedisUseCase;

    /**
     * OAuth2 사용자 신규 여부 확인.
     *
     * @param oAuthUserInfo 소셜로부터 받은 oAuth2UserInfo
     * @return 신규 사용자 여부
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isNewUser(OAuthUserInfo oAuthUserInfo) {
        return userRepositoryPort.findUserByProviderId(oAuthUserInfo.providerId()).isEmpty();
    }

    /**
     * 신규 사용자 처리.
     *
     * @param oAuthUserInfo OAuth2 유저정보
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
     * 기존 사용자 처리.
     * oAuth2UserInfo의 providerId를 토대로 기존유저의 정보를 반환한다.
     *
     * @param oAuthUserInfo OAuth2 유저정보
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
     * 이메일이 일치하는 유저를 db에서 찾아 로그인이 가능한 유저인지 확인한다.
     * @param email 로그인시 입력받은 이메일
     * @param password 로그인 시 입력받은 패스워드
     * @return 유저 정보
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
     * 비밀번호를 확인한다.
     *
     * @param userId 유저id
     * @param requestDto 비밀번호
     */
    @Override
    @Transactional(readOnly = true)
    public void confirmPassword(Long userId, ConfirmPasswordRequest requestDto) {
        User user = userRepositoryPort.findUserById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.BAD_REQUEST_LOGIN));

        boolean isMatched = user.isPasswordMatch(passwordEncoder, requestDto.password());
        if (!isMatched) {
            throw new UserException(UserErrorStatus.FAIL_CONFIRM_PASSWORD);
        }
    }
}
