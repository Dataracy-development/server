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
import com.dataracy.modules.auth.domain.model.vo.AuthUser;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.user.application.port.in.user.IsLoginPossibleUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandService implements SelfLoginUseCase, ReIssueTokenUseCase {
    private final JwtProperties jwtProperties;
    private final JwtGeneratorPort jwtGeneratorPort;
    private final JwtValidatorPort jwtValidatorPort;
    private final TokenRedisPort tokenRedisPort;

    private final IsLoginPossibleUseCase isLoginPossibleUseCase;

    /**
     * 이메일과 비밀번호로 사용자의 로그인을 수행하고 리프레시 토큰을 발급한다.
     *
     * @param requestDto 로그인 요청 정보(이메일, 비밀번호 등)
     * @return 발급된 리프레시 토큰과 만료 시간 정보가 포함된 응답 객체
     */
    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResponse login(SelfLoginRequest requestDto) {
        // 유저 db로부터 이메일이 일치하는 유저를 조회한다.
        UserInfo userInfo = isLoginPossibleUseCase.isLogin(requestDto.email(), requestDto.password());
        AuthUser authUser = AuthUser.from(userInfo);

        // 로그인 가능한 경우이므로 리프레시 토큰 발급
        String refreshToken = jwtGeneratorPort.generateRefreshToken(authUser.userId(), authUser.role());
        // 레디스에 리프레시 토큰 저장
        tokenRedisPort.saveRefreshToken(authUser.userId().toString(), refreshToken);

        log.info("자체 로그인 성공: {}", authUser.email());
        // 리프레시 토큰 반환
        return new RefreshTokenResponse(
                refreshToken,
                jwtProperties.getRefreshTokenExpirationTime()
        );
    }

    /**
     * 리프레시 토큰을 검증하고 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     *
     * 분산 락을 적용하여 동일한 리프레시 토큰으로의 동시 재발급을 방지합니다.
     * 저장된 리프레시 토큰과 입력된 토큰이 일치하는지 확인 후, 새로운 토큰을 생성하여 반환합니다.
     *
     * @param refreshToken 클라이언트가 제공한 리프레시 토큰
     * @return 새로 발급된 액세스 토큰과 리프레시 토큰, 각 만료 시간 정보가 포함된 응답 객체
     */
    @Override
    @DistributedLock(key = "'lock:refresh-reissue:' + #refreshToken", waitTime = 200, leaseTime = 3000)
    public ReIssueTokenResponse reIssueToken(String refreshToken) {
        try {
            // 쿠키의 리프레시 토큰으로 유저 아이디를 반환한다.
            Long userId = jwtValidatorPort.getUserIdFromToken(refreshToken);
            if (userId == null) {
                throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
            }

            // 레디스의 리프레시 토큰과 입력받은 리프레시 토큰을 비교한다.
            String savedRefreshToken = tokenRedisPort.getRefreshToken(userId.toString());
            if (!savedRefreshToken.equals(refreshToken)) {
                throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
            }

            // 어세스 토큰과 리프레시 토큰을 발급후 반환한다.
            RoleType userRole = jwtValidatorPort.getRoleFromToken(refreshToken);
            String newAccessToken = jwtGeneratorPort.generateAccessToken(userId, userRole);
            String newRefreshToken = jwtGeneratorPort.generateRefreshToken(userId, userRole);

            // 레디스에 리프레시 토큰 저장
            tokenRedisPort.saveRefreshToken(userId.toString(), newRefreshToken);

            // 재발급 코드 반환
            return new ReIssueTokenResponse(
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
