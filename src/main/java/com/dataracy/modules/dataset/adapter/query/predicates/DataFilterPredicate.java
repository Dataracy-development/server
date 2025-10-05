package com.dataracy.modules.dataset.adapter.query.predicates;

import static com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity.dataEntity;

import java.util.Collection;

import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;

public class DataFilterPredicate {
  /** 인스턴스 생성을 방지하기 위한 private 생성자입니다. */
  private DataFilterPredicate() {}

  /**
   * 삭제되지 않은 DataEntity를 필터링하는 Predicate를 반환합니다.
   *
   * <p>반환값은 DataEntity.isDeleted가 false인 레코드만 선택하는 QueryDSL의 BooleanExpression입니다.
   *
   * @return isDeleted가 false인 조건을 나타내는 BooleanExpression
   */
  public static BooleanExpression notDeleted() {
    return dataEntity.isDeleted.isFalse();
  }

  /**
   * 주어진 데이터 ID와 일치하는 데이터 엔터티를 필터링하는 QueryDSL 조건식을 반환합니다.
   *
   * @param dataId 필터링할 데이터 엔터티의 ID
   * @return 데이터 ID가 일치하는 경우의 BooleanExpression, dataId가 null이면 null 반환
   */
  public static BooleanExpression dataIdEq(Long dataId) {
    return dataId == null ? null : dataEntity.id.eq(dataId);
  }

  /**
   * 주어진 키워드가 데이터 엔티티의 제목 또는 설명에(대소문자 구분 없이) 포함되어 있는지 확인하는 QueryDSL 조건식을 반환합니다.
   *
   * @param keyword 검색할 키워드
   * @return 제목 또는 설명에 키워드가 포함되어 있으면 해당 조건식, 키워드가 비어 있거나 null이면 null
   */
  public static BooleanExpression keywordContains(String keyword) {
    if (!StringUtils.hasText(keyword)) return null;

    return dataEntity
        .title
        .containsIgnoreCase(keyword)
        .or(dataEntity.description.containsIgnoreCase(keyword));
  }

  /**
   * 주어진 topicId와 일치하는 데이터 엔터티만 필터링하는 QueryDSL BooleanExpression을 반환합니다.
   *
   * @param topicId 필터링할 토픽 ID
   * @return topicId가 null이 아니면 해당 topicId와 일치하는 조건의 BooleanExpression, null이면 null 반환
   */
  public static BooleanExpression topicIdEq(Long topicId) {
    return topicId == null ? null : dataEntity.topicId.eq(topicId);
  }

  /**
   * 주어진 dataSourceId와 일치하는 데이터 엔터티만 필터링하는 QueryDSL BooleanExpression을 반환합니다.
   *
   * @param dataSourceId 필터링할 데이터 소스의 ID
   * @return dataSourceId가 일치하는 경우에만 true인 BooleanExpression, dataSourceId가 null이면 null 반환
   */
  public static BooleanExpression dataSourceIdEq(Long dataSourceId) {
    return dataSourceId == null ? null : dataEntity.dataSourceId.eq(dataSourceId);
  }

  /**
   * 주어진 데이터 타입 ID와 일치하는 데이터 엔티티만 필터링하는 QueryDSL BooleanExpression을 반환합니다.
   *
   * @param dataTypeId 필터링할 데이터 타입 ID (null이면 조건식이 생성되지 않음)
   * @return 데이터 타입 ID가 일치하는 경우의 BooleanExpression, 또는 dataTypeId가 null이면 null
   */
  public static BooleanExpression dataTypeIdEq(Long dataTypeId) {
    return dataTypeId == null ? null : dataEntity.dataTypeId.eq(dataTypeId);
  }

  /**
   * 지정한 데이터 ID 목록에 해당하는 레코드들을 필터링하는 QueryDSL Predicate를 생성한다.
   *
   * <p>지정한 ID 컬렉션을 이용해 `dataEntity.id.in(...)` 형태의 BooleanExpression을 반환한다. 입력이 null이거나 비어 있으면 필터가
   * 적용되지 않도록 null을 반환한다.
   *
   * @param dataIds 필터에 사용할 데이터 ID 목록 (null 또는 비어있으면 필터 미적용)
   * @return ID 목록에 해당하는 Predicate 또는 입력이 null/빈 경우 null
   */
  public static BooleanExpression dataIdIn(Collection<Long> dataIds) {
    return (dataIds == null || dataIds.isEmpty()) ? null : dataEntity.id.in(dataIds);
  }

  /**
   * 지정한 사용자 ID로 DataEntity.userId를 필터링하는 Predicate를 반환합니다.
   *
   * <p>null을 전달하면 필터를 적용하지 않기 위해 null을 반환합니다.
   *
   * @param userId 필터에 사용할 사용자 ID (null이면 필터 미적용)
   * @return userId가 null이 아니면 DataEntity.userId.eq(userId) Predicate, 그렇지 않으면 null
   */
  public static BooleanExpression userIdEq(Long userId) {
    return userId == null ? null : dataEntity.userId.eq(userId);
  }
}
