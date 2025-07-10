package com.dataracy.modules.behaviorlog.support.parser;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class UserAgentParser {

    private static final UserAgentAnalyzer analyzer = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withField("OperatingSystemName")
            .withField("AgentName") // 브라우저 이름
            .build();

    public static String extractOS(String userAgent) {
        try {
            UserAgent parsed = analyzer.parse(userAgent);
            return parsed.getValue("OperatingSystemName");
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    public static String extractBrowser(String userAgent) {
        try {
            UserAgent parsed = analyzer.parse(userAgent);
            return parsed.getValue("AgentName");
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
