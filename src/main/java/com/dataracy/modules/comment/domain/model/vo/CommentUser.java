/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record CommentUser(Long userId, RoleType role, String nickname, Long authorLevelId) {
  /**
   * UserInfo 객체의 정보를 기반으로 CommentUser 인스턴스를 생성합니다.
   *
   * @param info CommentUser로 변환할 사용자 정보
   * @return UserInfo의 필드를 그대로 매핑한 CommentUser 인스턴스
   */
  public static CommentUser fromUserInfo(UserInfo info) {
    return new CommentUser(info.id(), info.role(), info.nickname(), info.authorLevelId());
  }
}
