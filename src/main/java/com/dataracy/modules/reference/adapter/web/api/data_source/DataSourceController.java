package com.dataracy.modules.reference.adapter.web.api.data_source;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.DataSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.port.in.data_source.FindAllDataSourcesUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DataSourceController implements DataSourceApi {
    private final DataSourceWebMapper dataSourceWebMapper;
    private final FindAllDataSourcesUseCase findAllDataSourcesUseCase;
    /**
     * 전체 데이터 출처 목록 조회
     */
    @Override
    public ResponseEntity<SuccessResponse<AllDataSourcesWebResponse>> allDataSources (
    ) {
        AllDataSourcesResponse allDataSourcesResponse = findAllDataSourcesUseCase.allDataSources();
        AllDataSourcesWebResponse allDataSourcesWebResponse = dataSourceWebMapper.toWebDto(allDataSourcesResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_DATA_SOURCE_LIST, allDataSourcesWebResponse));
    }
}
