package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;

import java.util.Collection;

public interface FindDataLabelMapUseCase {
    DataLabelMapResponse labelMapping(Collection<DataWithProjectCountDto> savedDataSets);
}
