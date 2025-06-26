package com.dataracy.common.util;

import com.dataracy.user.status.AuthErrorStatus;
import com.dataracy.user.status.AuthException;
import jakarta.servlet.http.HttpServletRequest;

public class ExtractHeaderUtil {

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     * @param request HttpServletRequest
     * @return 헤더에서 어세스 토큰 추출
     */
    public static String extractAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthException(AuthErrorStatus.NOT_FOUND_ACCESS_TOKEN_IN_HEADER);
        }

        return authorizationHeader.substring(7);
    }
}
