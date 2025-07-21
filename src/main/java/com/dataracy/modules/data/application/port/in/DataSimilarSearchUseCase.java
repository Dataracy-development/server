package com.dataracy.modules.data.application.port.in;

import com.dataracy.modules.data.application.dto.response.DataSimilarSearchResponse;

import java.util.List;

public interface DataSimilarSearchUseCase {
List<DataSimilarSearchResponse> findSimilarDataSets(Long dataId, int size);
}
