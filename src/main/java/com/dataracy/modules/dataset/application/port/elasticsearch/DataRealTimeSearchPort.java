package com.dataracy.modules.dataset.application.port.elasticsearch;

import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;

import java.util.List;

public interface DataRealTimeSearchPort {
List<DataMinimalSearchResponse> search(String keyword, int size);
}
