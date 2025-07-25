package com.dataracy.modules.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public final class ExtractHeaderUtil {
    private ExtractHeaderUtil() {
    }

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
}
