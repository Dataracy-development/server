package com.dataracy.modules.like.adapter.jpa.mapper;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.model.Like;

public final class LikeEntityMapper {
  private LikeEntityMapper() {}

  /**
   * LikeEntity 객체를 Like 도메인 객체로 변환합니다.
   *
   * @param entity 변환할 LikeEntity 객체
   * @return 변환된 Like 도메인 객체. 입력이 null이면 null을 반환합니다.
   */
  public static Like toDomain(LikeEntity entity) {
    if (entity == null) return null;

    return Like.of(
        entity.getId(), entity.getTargetId(), entity.getTargetType(), entity.getUserId());
  }

  /**
   * Like 도메인 객체를 LikeEntity로 변환합니다.
   *
   * @param like 변환할 Like 도메인 객체
   * @return 변환된 LikeEntity 객체, 입력이 null이면 null 반환
   */
  public static LikeEntity toEntity(Like like) {
    if (like == null) return null;

    return LikeEntity.of(like.getTargetId(), like.getTargetType(), like.getUserId());
  }
}
