package com.dataracy.modules.data.application.port.elasticsearch;

import com.dataracy.modules.data.application.dto.response.DataSimilarSearchResponse;
import com.dataracy.modules.data.domain.model.Data;

import java.util.List;

public interface DataSimilarSearchPort {
List<DataSimilarSearchResponse> recommendSimilarDataSets(Data data, int size);
}
