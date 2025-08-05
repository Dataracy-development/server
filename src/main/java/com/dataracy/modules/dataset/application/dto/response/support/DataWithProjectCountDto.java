package com.dataracy.modules.dataset.application.dto.response.support;

import com.dataracy.modules.dataset.domain.model.Data;

public record DataWithProjectCountDto(
        Data data,
        Long countConnectedProjects
) {}
