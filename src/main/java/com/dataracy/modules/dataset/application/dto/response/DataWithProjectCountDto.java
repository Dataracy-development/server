package com.dataracy.modules.dataset.application.dto.response;

import com.dataracy.modules.dataset.domain.model.Data;

public record DataWithProjectCountDto(
        Data data,
        Long countConnectedProjects
) {}
