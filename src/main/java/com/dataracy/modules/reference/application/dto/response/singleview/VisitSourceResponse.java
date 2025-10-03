/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.dto.response.singleview;

/**
 * 방문 경로 애플리케이션 응답 DTO
 *
 * @param id 아이디
 * @param value 값
 * @param label 라벨
 */
public record VisitSourceResponse(Long id, String value, String label) {}
