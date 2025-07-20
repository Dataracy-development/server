package com.dataracy.modules.project.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

import static com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity.projectEntity;

public class ProjectFilterPredicate {
    /**
 * 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectFilterPredicate() {}

    /**
     * 주어진 프로젝트 ID와 일치하는 ProjectEntity를 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param projectId 필터링할 프로젝트의 ID
     * @return 프로젝트 ID가 일치할 때 true인 BooleanExpression, projectId가 null이면 null 반환
     */
    public static BooleanExpression projectIdEq(Long projectId) {
        return projectId == null ? null : projectEntity.id.eq(projectId);
    }

    /**
     * 프로젝트 제목에 주어진 키워드가 포함되어 있는지(대소문자 구분 없이) 확인하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param keyword 필터링에 사용할 키워드
     * @return 키워드가 포함된 프로젝트를 찾는 BooleanExpression, 키워드가 비어 있거나 null이면 null 반환
     */
    public static BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;

        return projectEntity.title.containsIgnoreCase(keyword);
    }


    /**
     * 주어진 토픽 ID와 일치하는 프로젝트를 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param topicId 필터링할 토픽 ID
     * @return 토픽 ID가 일치하는 조건의 BooleanExpression, topicId가 null이면 null 반환
     */
    public static BooleanExpression topicIdEq(Long topicId) {
        return topicId == null ? null : projectEntity.topicId.eq(topicId);
    }

    /**
     * 분석 목적 ID가 주어진 값과 일치하는 프로젝트에 대한 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param analysisPurposeId 필터링할 분석 목적 ID
     * @return 일치하는 경우 해당 조건의 BooleanExpression, 입력값이 null이면 null 반환
     */
    public static BooleanExpression analysisPurposeIdEq(Long analysisPurposeId) {
        return analysisPurposeId == null ? null : projectEntity.analysisPurposeId.eq(analysisPurposeId);
    }

    /**
     * 주어진 데이터 소스 ID와 일치하는 프로젝트를 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param dataSourceId 필터링할 데이터 소스 ID
     * @return 데이터 소스 ID가 일치하는 경우 해당 조건의 BooleanExpression, dataSourceId가 null이면 null 반환
     */
    public static BooleanExpression dataSourceIdEq(Long dataSourceId) {
        return dataSourceId == null ? null : projectEntity.dataSourceId.eq(dataSourceId);
    }

    /**
     * 주어진 authorLevelId와 동일한 authorLevelId를 가진 프로젝트에 대한 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param authorLevelId 필터링할 작성자 레벨 ID
     * @return authorLevelId가 일치하는 프로젝트를 위한 BooleanExpression, authorLevelId가 null이면 null 반환
     */
    public static BooleanExpression authorLevelIdEq(Long authorLevelId) {
        return authorLevelId == null ? null : projectEntity.authorLevelId.eq(authorLevelId);
    }
}
