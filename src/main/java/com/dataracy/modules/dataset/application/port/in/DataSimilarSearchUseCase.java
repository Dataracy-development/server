package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataSimilarSearchResponse;

import java.util.List;

public interface DataSimilarSearchUseCase {
List<DataSimilarSearchResponse> findSimilarDataSets(Long dataId, int size);
}
