package com.dataracy.modules.user.application;

import com.dataracy.modules.auth.application.JwtApplicationService;
import com.dataracy.modules.auth.application.JwtQueryService;
import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.user.application.dto.response.RefreshTokenResponseDto;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.repository.UserRepository;
import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final JwtQueryService jwtQueryService;
    private final JwtApplicationService jwtApplicationService;
    private final PasswordEncoder passwordEncoder;

    /**
     * OAuth2 사용자 신규 여부 확인.
     *
     * @param oAuth2UserInfo 소셜로부터 받은 oAuth2UserInfo
     * @return 신규 사용자 여부
     */
    @Transactional(readOnly = true)
    public boolean isNewUser(OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.findUserByProviderId(oAuth2UserInfo.getProviderId()) == null;
    }

    /**
     * 기존 사용자 처리.
     * oAuth2UserInfo의 providerId를 토대로 기존유저의 정보를 반환한다.
     *
     * @param oAuth2UserInfo    OAuth2 유저정보
     */
    @Transactional(readOnly = true)
    public RefreshTokenResponseDto handleExistingUser(OAuth2UserInfo oAuth2UserInfo) {
        User existUser = userRepository.findUserByProviderId(oAuth2UserInfo.getProviderId());
        long refreshTokenExpirationTime = jwtQueryService.getRefreshTokenExpirationTime();
        String refreshToken = jwtApplicationService.generateAccessOrRefreshToken(existUser.getId(), existUser.getRole(), refreshTokenExpirationTime);
        log.info("기존 사용자 처리 완료: {}", existUser.getId());
        return new RefreshTokenResponseDto(existUser.getId(), refreshToken, refreshTokenExpirationTime);
    }

    @Transactional(readOnly = true)
    public User getUserNotDuplicatedEmailAndPassword(String email, String password){
        User user = userRepository.findUserByEmail(email);
        if (user == null || userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorStatus.BAD_REQUEST_DUPLICATE_EMAIL);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorStatus.BAD_REQUEST_DUPLICATE_PASSWORD);
        }
        return user;
    }
}
