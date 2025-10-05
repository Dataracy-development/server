package com.dataracy.modules.dataset.domain.model.vo;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record DataUser(
    Long userId,
    RoleType role,
    String email,
    String nickname,
    String profileImageUrl,
    String introductionText,
    Long occupationId,
    Long authorLevelId) {
  /**
   * UserInfo 객체로부터 DataUser 인스턴스를 생성합니다.
   *
   * <p>info의 필드(id, role, email, nickname, profileImageUrl, introductionText, occupationId,
   * authorLevelId)를 DataUser의 대응 필드로 매핑하여 반환합니다. info가 null이면 경고를 기록한 후
   * DataException(DataErrorStatus.FAIL_GET_USER_INFO)을 던집니다.
   *
   * @param info 변환할 사용자 정보 (null일 수 없음)
   * @return 변환된 DataUser 인스턴스
   * @throws DataException info가 null인 경우 발생
   */
  public static DataUser fromUserInfo(UserInfo info) {
    if (info == null) {
      LoggerFactory.domain().logWarning("DataUser을 생성하기 위한 유저 정보가 주입되지 않았습니다.");
      throw new DataException(DataErrorStatus.FAIL_GET_USER_INFO);
    }
    return new DataUser(
        info.id(),
        info.role(),
        info.email(),
        info.nickname(),
        info.profileImageUrl(),
        info.introductionText(),
        info.occupationId(),
        info.authorLevelId());
  }
}
