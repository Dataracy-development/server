package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.CountDataGroupResponse;

import java.util.List;

public interface CountDataGroupByTopicLabelUseCase {
    List<CountDataGroupResponse> countDataGroups();
}
