package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataDetailResponse;

public interface DataDetailUseCase {
    DataDetailResponse getDataDetail(Long dataId);
}
