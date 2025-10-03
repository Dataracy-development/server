/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.support.parser;

import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@Slf4j
public class UserAgentParser {

  private static final UserAgentAnalyzer ANALYZER = createAnalyzer();

  private static UserAgentAnalyzer createAnalyzer() {
    try {
      return UserAgentAnalyzer.newBuilder()
          .hideMatcherLoadStats()
          .withField("OperatingSystemName")
          .withField("AgentName") // 브라우저 이름
          .build();
    } catch (Exception e) {
      log.error("UserAgentAnalyzer 초기화 실패", e);
      throw new RuntimeException("UserAgentAnalyzer 초기화 실패", e);
    }
  }

  /** Utility 클래스이므로 인스턴스화를 방지합니다. */
  private UserAgentParser() {
    // SpotBugs CT_CONSTRUCTOR_THROW 경고 해결을 위해 예외를 던지지 않음
    // 대신 생성자를 private으로 만들어 인스턴스화를 방지
  }

  /**
   * 주어진 User-Agent 문자열에서 운영체제 이름을 추출합니다.
   *
   * @param userAgent 분석할 User-Agent 문자열
   * @return 추출된 운영체제 이름, 추출에 실패하면 "UNKNOWN" 반환
   */
  public static String extractOS(String userAgent) {
    try {
      UserAgent parsed = ANALYZER.parse(userAgent);
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
      UserAgent parsed = ANALYZER.parse(userAgent);
      return parsed.getValue("AgentName");
    } catch (Exception e) {
      log.debug("사용자 에이전트 OS 파싱 실패: {}", userAgent, e);
      return "UNKNOWN";
    }
  }
}
