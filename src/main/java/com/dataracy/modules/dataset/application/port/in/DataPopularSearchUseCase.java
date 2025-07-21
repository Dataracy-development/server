package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataPopularSearchResponse;

import java.util.List;

public interface DataPopularSearchUseCase {
    List<DataPopularSearchResponse> findPopularDataSets(int size);
}
