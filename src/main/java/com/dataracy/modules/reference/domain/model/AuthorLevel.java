/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.domain.model;

/**
 * 작성자 유형 도메인 모델
 *
 * @param id 작성자 유형 id
 * @param value 작성자 유형 값
 * @param label 작성자 유형 라벨
 */
public record AuthorLevel(Long id, String value, String label) {}
