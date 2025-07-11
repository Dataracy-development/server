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

    /**
     * 주어진 User-Agent 문자열에서 운영체제 이름을 추출합니다.
     *
     * @param userAgent 분석할 User-Agent 문자열
     * @return 추출된 운영체제 이름, 추출에 실패하면 "UNKNOWN" 반환
     */
    public static String extractOS(String userAgent) {
        try {
            UserAgent parsed = analyzer.parse(userAgent);
            return parsed.getValue("OperatingSystemName");
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * 주어진 사용자 에이전트 문자열에서 브라우저 이름을 추출합니다.
     *
     * @param userAgent 브라우저 및 운영체제 정보를 포함하는 사용자 에이전트 문자열
     * @return 추출된 브라우저 이름, 추출에 실패하면 "UNKNOWN" 반환
     */
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
