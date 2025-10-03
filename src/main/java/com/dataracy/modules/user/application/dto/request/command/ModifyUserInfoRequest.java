/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.dto.request.command;

import java.util.List;

/**
 * 회원 정보 수정을 위한 애플리케이션 요청 DTO
 *
 * @param nickname 닉네임
 * @param authorLevelId 작성자 유형 아이디
 * @param occupationId 직업 아이디
 * @param topicIds 흥미있픽 토 아이디 리스트
 * @param visitSourceId 방문 경로 아이디
 * @param introductionText 자기 소개 글
 */
public record ModifyUserInfoRequest(
    String nickname,
    Long authorLevelId,
    Long occupationId,
    List<Long> topicIds,
    Long visitSourceId,
    String introductionText) {}
