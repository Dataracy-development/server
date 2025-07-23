package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
import com.dataracy.modules.dataset.application.dto.response.DataFilterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataFilteredSearchUseCase {
Page<DataFilterResponse> findFilteredDataSets(DataFilterRequest requestDto, Pageable pageable);
}
