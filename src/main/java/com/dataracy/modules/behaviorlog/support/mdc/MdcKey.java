/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.support.mdc;

/** MDC 키 상수 정의 */
public final class MdcKey {

  public static final String REQUEST_ID = "requestId";
  public static final String SESSION_ID = "sessionId";
  public static final String USER_ID = "userId";
  public static final String ANONYMOUS_ID = "anonymousId";
  public static final String ACTION = "action";
  public static final String IP = "ip";
  public static final String PATH = "path";
  public static final String METHOD = "method";
  public static final String DATA_ACCESS_LATENCY = "dataAccessLatency";
  public static final String REFERRER = "referrer";
  public static final String STAY_TIME = "stayTime";
  public static final String NEXT_PATH = "nextPath";

  /** 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다. */
  private MdcKey() {}
}
