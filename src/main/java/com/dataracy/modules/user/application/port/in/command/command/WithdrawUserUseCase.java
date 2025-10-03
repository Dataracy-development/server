/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.port.in.command.command;

public interface WithdrawUserUseCase {
  /**
   * 지정된 사용자 ID에 해당하는 사용자를 탈퇴 처리한다.
   *
   * @param userId 탈퇴 대상 사용자의 고유 식별자(식별 불가능한 경우 null 허용 여부는 구현에 따름)
   */
  void withdrawUser(Long userId);
}
