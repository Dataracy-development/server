package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import com.dataracy.modules.common.lock.DistributedLock;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponseDto;
import com.dataracy.modules.user.application.UserQueryService;
import com.dataracy.modules.user.application.dto.request.SelfLoginRequestDto;
import com.dataracy.modules.user.application.dto.response.RefreshTokenResponseDto;
import com.dataracy.modules.user.domain.enums.RoleStatusType;
import com.dataracy.modules.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JwtApplicationService jwtApplicationService;
    private final JwtQueryService jwtQueryService;
    private final TokenApplicationService tokenApplicationService;
    private final UserQueryService userQueryService;

    /**
     * 클라이언트로부터 받은 이메일과 비밀번호로 로그인을 진행한다.
     *
     * @param requestDto 자체 로그인을 위한 Dto
     * @return LoginResponseDto (컨트롤러에서 리프레시 토큰 쿠키 저장을 위한 response)
     */
    @Transactional(readOnly = true)
    public RefreshTokenResponseDto login(SelfLoginRequestDto requestDto) {
        User user = userQueryService.getUserNotDuplicatedEmailAndPassword(requestDto.email(), requestDto.password());

        String refreshToken = jwtApplicationService.generateAccessOrRefreshToken(user.getId(), user.getRole(), jwtQueryService.getRefreshTokenExpirationTime());

        log.info("자체 로그인 성공: {}", user.getEmail());
        return new RefreshTokenResponseDto(user.getId(), refreshToken, jwtQueryService.getRefreshTokenExpirationTime());
    }

    /**
     * Refresh Token 검증 및 새로운 토큰 발급.
     *
     * @param refreshToken 클라이언트로부터 받은 리프레시 토큰
     * @return 새로 생성된 Access Token과 Refresh Token
     */
    @DistributedLock(key = "'lock:refresh-reissue:' + #refreshToken", waitTime = 200, leaseTime = 3000)
    public ReIssueTokenResponseDto reIssueToken(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }

        Long userId = tokenApplicationService.validateAndExtractUserId(refreshToken);
        String newAccessToken = jwtApplicationService.generateAccessOrRefreshToken(userId, RoleStatusType.ROLE_USER, jwtQueryService.getAccessTokenExpirationTime());
        String newRefreshToken = jwtApplicationService.generateAccessOrRefreshToken(userId, RoleStatusType.ROLE_USER, jwtQueryService.getRefreshTokenExpirationTime());
        return new ReIssueTokenResponseDto(userId, newAccessToken, newRefreshToken, jwtQueryService.getAccessTokenExpirationTime(), jwtQueryService.getRefreshTokenExpirationTime());
    }
}
