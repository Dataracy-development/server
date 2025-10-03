/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.adapter.jpa.impl.validate;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.validate.ValidateUserExistsPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CheckUserExistsDbAdapter implements ValidateUserExistsPort {
  private final UserJpaRepository userJpaRepository;

  // Entity 상수 정의
  private static final String USER_ENTITY = "UserEntity";

  /**
   * 주어진 닉네임을 가진 사용자가 존재하는지 확인합니다.
   *
   * @param nickname 존재 여부를 확인할 사용자의 닉네임
   * @return 닉네임을 가진 사용자가 존재하면 true, 그렇지 않으면 false
   */
  @Override
  public boolean existsByNickname(String nickname) {
    boolean exists = userJpaRepository.existsByNickname(nickname);
    LoggerFactory.db()
        .logExist(
            USER_ENTITY,
            "[existsByNickname] 해당 닉네임에 해당하는 유저가 존재하는지 유무를 확인하였습니다. nickname=" + nickname);
    return exists;
  }

  /**
   * 주어진 이메일을 가진 사용자가 존재하는지 확인합니다.
   *
   * @param email 존재 여부를 확인할 사용자의 이메일
   * @return 사용자가 존재하면 true, 그렇지 않으면 false
   */
  @Override
  public boolean existsByEmail(String email) {
    boolean exists = userJpaRepository.existsByEmail(email);
    LoggerFactory.db()
        .logExist(
            USER_ENTITY, "[existsByEmail] 해당 이메일에 해당하는 유저가 존재하는지 유무를 확인하였습니다. email=" + email);
    return exists;
  }
}
