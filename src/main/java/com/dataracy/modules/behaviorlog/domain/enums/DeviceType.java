package com.dataracy.modules.behaviorlog.domain.enums;

/**
 * 접속한 디바이스 타입
 */
public enum DeviceType {
    PC, MOBILE, TABLET, UNKNOWN;

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
