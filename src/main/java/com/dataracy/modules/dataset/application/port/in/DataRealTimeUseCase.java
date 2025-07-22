package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;

import java.util.List;

public interface DataRealTimeUseCase {
List<DataMinimalSearchResponse> findRealTimeDataSets(String keyword, int size);
}
