package com.dataracy.modules.dataset.application.dto.response.support;

import com.dataracy.modules.dataset.domain.model.Data;

/**
 *요청
 * @param data
 * @param countConnectedProjects
 */
public record DataWithProjectCountDto(
        Data data,
        Long countConnectedProjects
) {}
