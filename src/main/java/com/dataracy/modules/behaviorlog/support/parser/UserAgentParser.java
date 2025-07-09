package com.dataracy.modules.behaviorlog.support.parser;

import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;

/**
 * User-Agent를 파싱하여 OS, 브라우저 정보를 추출하는 도우미 클래스
 */
public class UserAgentParser {

    private static final Parser parser;

    static {
        try {
            parser = new Parser();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractOS(String userAgent) {
        try {
            Client client = parser.parse(userAgent);
            return client.os.family; // 예: "Windows", "iOS"
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    public static String extractBrowser(String userAgent) {
        try {
            Client client = parser.parse(userAgent);
            return client.userAgent.family; // 예: "Chrome", "Safari"
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
