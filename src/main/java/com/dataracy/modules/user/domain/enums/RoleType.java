package com.dataracy.modules.user.domain.enums;

import java.util.Arrays;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

import lombok.Getter;

@Getter
public enum RoleType {
  ROLE_USER("ROLE_USER"),
  ROLE_ADMIN("ROLE_ADMIN"),
  ROLE_ANONYMOUS("ROLE_ANONYMOUS");

  private final String value;

  RoleType(String value) {
    this.value = value;
  }

  /**
   * 주어진 문자열을 기반으로 일치하는 RoleType 열거형 상수를 반환합니다.
   *
   * <p>입력값이 RoleType의 value 또는 이름과(대소문자 무시) 일치하지 않으면 규칙 위반 로그를 남기고 UserException을 발생시킵니다.
   *
   * @param input 역할을 나타내는 문자열
   * @return 일치하는 RoleType 열거형 상수
   * @throws UserException 입력값이 유효한 역할이 아닐 경우 발생
   */
  public static RoleType of(String input) {
    return Arrays.stream(RoleType.values())
        .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
        .findFirst()
        .orElseThrow(
            () -> {
              LoggerFactory.domain()
                  .logRuleViolation(
                      "RoleType", "잘못된 ENUM 타입입니다. ROLE_USER, ROLE_ADMIN, ROLE_ANONYMOUS만 가능합니다.");
              return new UserException(UserErrorStatus.INVALID_ROLE_TYPE);
            });
  }
}
