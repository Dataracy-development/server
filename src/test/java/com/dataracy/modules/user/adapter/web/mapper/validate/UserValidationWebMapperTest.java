/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.adapter.web.mapper.validate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.user.adapter.web.request.validate.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;

class UserValidationWebMapperTest {

  private UserValidationWebMapper mapper = new UserValidationWebMapper();

  @Test
  @DisplayName("toApplicationDto: 닉네임 매핑")
  void toApplicationDto() {
    // given
    DuplicateNicknameWebRequest web = new DuplicateNicknameWebRequest("nick");

    // when
    DuplicateNicknameRequest dto = mapper.toApplicationDto(web);

    // then
    assertThat(dto.nickname()).isEqualTo("nick");
  }
}
