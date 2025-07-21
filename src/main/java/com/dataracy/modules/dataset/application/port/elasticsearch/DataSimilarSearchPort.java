package com.dataracy.modules.dataset.application.port.elasticsearch;

import com.dataracy.modules.dataset.application.dto.response.DataSimilarSearchResponse;
import com.dataracy.modules.dataset.domain.model.Data;

import java.util.List;

public interface DataSimilarSearchPort {
List<DataSimilarSearchResponse> recommendSimilarDataSets(Data data, int size);
}
