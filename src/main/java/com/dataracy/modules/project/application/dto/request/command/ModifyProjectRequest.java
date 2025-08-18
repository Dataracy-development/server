package com.dataracy.modules.project.application.dto.request.command;

import java.util.List;

/**
 * 프로젝트 수정 애플리케이션 요청 DTO
 *
 * @param title 프로젝트 제목
 * @param topicId 토픽 아이디
 * @param analysisPurposeId 분석 목적 아이디
 * @param dataSourceId 데이터 출처 아이디
 * @param authorLevelId 작성자 유형 아이디
 * @param isContinue 프로젝트 이어가기 유무
 * @param parentProjectId 부모 프로젝트 아이디
 * @param content 프로젝트 내용
 * @param dataIds 프로젝트에 연결된 데이터 아이디 목록
 */
public record ModifyProjectRequest(
        String title,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId,
        Boolean isContinue,
        Long parentProjectId,
        String content,
        List<Long> dataIds
) {}
