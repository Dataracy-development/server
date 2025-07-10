package com.dataracy.modules.behaviorlog.domain.enums;

/**
 * 접속한 디바이스 타입
 */
public enum DeviceType {
    PC, MOBILE, TABLET, UNKNOWN;

    /**
     * 주어진 User-Agent 문자열을 기반으로 디바이스 유형을 판별합니다.
     *
     * @param userAgent 디바이스의 User-Agent 문자열
     * @return User-Agent에 따라 MOBILE, TABLET, PC, UNKNOWN 중 하나의 DeviceType을 반환합니다. userAgent가 null이면 UNKNOWN을 반환합니다.
     */
    public static DeviceType resolve(String userAgent) {
        if (userAgent == null) return UNKNOWN;

        String ua = userAgent.toLowerCase();

        if (ua.contains("mobi") || ua.contains("android") || ua.contains("iphone")) {
            return MOBILE;
        }

        if (ua.contains("ipad") || ua.contains("tablet") || ua.contains("kindle")) {
            return TABLET;
        }

        if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux") || ua.contains("x11")) {
            return PC;
        }

        return UNKNOWN;
    }
}
