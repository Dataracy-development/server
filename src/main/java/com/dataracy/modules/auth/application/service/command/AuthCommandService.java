package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.auth.application.port.out.redis.TokenRedisPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.user.application.port.in.auth.IsLoginPossibleUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandService implements SelfLoginUseCase, ReIssueTokenUseCase {
    private final JwtGeneratorPort jwtGeneratorPort;
    private final JwtValidatorPort jwtValidatorPort;
    private final JwtProperties jwtProperties;

    private final TokenRedisPort tokenRedisPort;

    private final IsLoginPossibleUseCase isLoginPossibleUseCase;
    private final PasswordEncoder passwordEncoder;

    /**
     * 클라이언트로부터 받은 이메일과 비밀번호로 로그인을 진행한다.
     *
     * @param requestDto 자체 로그인을 위한 Dto
     * @return LoginResponseDto (컨트롤러에서 리프레시 토큰 쿠키 저장을 위한 response)
     */
    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResponse login(SelfLoginRequest requestDto) {
        // 유저 db로부터 이메일이 일치하는 유저를 조회한다.
        User user = isLoginPossibleUseCase.findUserByEmail(requestDto.email());

        // 이메일 또는 패스워드가 일치하지 않을 경우
        if (user == null || !passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new AuthException(AuthErrorStatus.BAD_REQUEST_LOGIN);
        }

        // 로그인 가능한 경우이므로 리프레시 토큰 발급
        String refreshToken = jwtGeneratorPort.generateRefreshToken(user.getId(), user.getRole());

        log.info("자체 로그인 성공: {}", user.getEmail());
        return new RefreshTokenResponse(
                user.getId(),
                refreshToken,
                jwtProperties.getRefreshTokenExpirationTime()
        );
    }

    /**
     * Refresh Token 검증 및 새로운 토큰 발급.
     *
     * @param refreshToken 클라이언트로부터 받은 리프레시 토큰
     * @return 새로 생성된 Access Token과 Refresh Token
     */
    @Override
    @DistributedLock(key = "'lock:refresh-reissue:' + #refreshToken", waitTime = 200, leaseTime = 3000)
    public ReIssueTokenResponse reIssueToken(String refreshToken) {
        try {
            // 레디스의 리프레시 토큰과 입력받은 리프레시 토큰을 비교한다.
            Long userId = jwtValidatorPort.getUserIdFromToken(refreshToken);
            String savedRefreshToken = tokenRedisPort.getRefreshToken(userId.toString());
            if (!savedRefreshToken.equals(refreshToken)) {
                throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
            }

            // 어세스 토큰과 리프레시 토큰을 발급후 반환한다.
            String newAccessToken = jwtGeneratorPort.generateAccessToken(userId, RoleType.ROLE_USER);
            String newRefreshToken = jwtGeneratorPort.generateRefreshToken(userId, RoleType.ROLE_USER);
            return new ReIssueTokenResponse(
                    userId,
                    newAccessToken,
                    newRefreshToken,
                    jwtProperties.getAccessTokenExpirationTime(),
                    jwtProperties.getRefreshTokenExpirationTime()
            );
        } catch (AuthException e) {
            if (e.getErrorCode() == AuthErrorStatus.EXPIRED_TOKEN) {
                throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
            } else if (e.getErrorCode() == AuthErrorStatus.INVALID_TOKEN) {
                throw new AuthException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
            } else {
                throw e;
            }
        } catch (Exception e) {
            log.error("Unhandled error", e);
            throw e;
        }
    }
}
