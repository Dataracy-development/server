package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.mapper.DataSourceDtoMapper;
import com.dataracy.modules.reference.application.port.in.data_source.FindAllDataSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.data_source.FindDataSourceUseCase;
import com.dataracy.modules.reference.application.port.out.DataSourceRepositoryPort;
import com.dataracy.modules.reference.domain.model.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceQueryService implements
        FindAllDataSourcesUseCase,
        FindDataSourceUseCase
{
    private final DataSourceDtoMapper dataSourceDtoMapper;
    private final DataSourceRepositoryPort dataSourceRepositoryPort;

    /**
     * 모든 dataSource 리스트를 조회한다.
     * @return dataSource 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public AllDataSourcesResponse allDataSources() {
        List<DataSource> dataSources = dataSourceRepositoryPort.allDataSources();
        return dataSourceDtoMapper.toResponseDto(dataSources);
    }

    /**
     * 데이터 출처 id로 데이터 출처을 조회한다.
     * @param dataSourceId 데이터 출처 id
     * @return 데이터 출처
     */
    @Override
    @Transactional(readOnly = true)
    public AllDataSourcesResponse.DataSourceResponse findDataSource(Long dataSourceId) {
        DataSource dataSource = dataSourceRepositoryPort.findDataSourceById(dataSourceId);
        return dataSourceDtoMapper.toResponseDto(dataSource);
    }
}
