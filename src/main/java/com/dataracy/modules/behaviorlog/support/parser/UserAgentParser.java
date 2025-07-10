package com.dataracy.modules.behaviorlog.support.parser;

import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@Slf4j
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
            log.debug("사용자 에이전트 OS 파싱 실패: {}", userAgent, e);
            return "UNKNOWN";
        }
    }
}
