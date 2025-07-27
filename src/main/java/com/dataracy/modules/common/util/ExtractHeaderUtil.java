package com.dataracy.modules.common.util;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExtractHeaderUtil {
    private final JwtValidateUseCase jwtValidateUseCase;

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     * @param request HttpServletRequest
     * @return 헤더에서 어세스 토큰 추출
     */
    public static Optional<String> extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(header.substring(7));
    }

    public Long extractAuthenticatedUserIdFromRequest(HttpServletRequest request) {
        try {
            return extractAccessToken(request)
                    .map(jwtValidateUseCase::getUserIdFromToken)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public String extractViewerIdFromRequest(HttpServletRequest request, HttpServletResponse response) {
        // 인증된 유저라면 accessToken → userId 추출
        try {
            Optional<String> accessToken = extractAccessToken(request);
            if (accessToken.isPresent()) {
                Long userId = jwtValidateUseCase.getUserIdFromToken(accessToken.get());
                if (userId != null) {
                    return String.valueOf(userId);
                }
            }
        } catch (Exception ignored) {
            // 인증 실패 시 무시하고 anonymousId로 대체
            return CookieUtil.getOrCreateAnonymousId(request, response);
        }
        // anonymousId 반환
        return CookieUtil.getOrCreateAnonymousId(request, response);
    }
}
