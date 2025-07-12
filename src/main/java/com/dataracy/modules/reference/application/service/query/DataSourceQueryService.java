package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.application.mapper.DataSourceDtoMapper;
import com.dataracy.modules.reference.application.port.in.data_source.FindAllDataSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.data_source.FindDataSourceUseCase;
import com.dataracy.modules.reference.application.port.out.DataSourceRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataSource;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
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
     * 모든 데이터 소스의 목록을 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 데이터 소스 정보를 담은 AllDataSourcesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllDataSourcesResponse allDataSources() {
        List<DataSource> dataSources = dataSourceRepositoryPort.allDataSources();
        return dataSourceDtoMapper.toResponseDto(dataSources);
    }

    /**
     * 주어진 ID로 데이터 출처를 조회하여 응답 DTO로 반환한다.
     * 데이터 출처가 존재하지 않을 경우 ReferenceException을 발생시킨다.
     *
     * @param dataSourceId 조회할 데이터 출처의 ID
     * @return 데이터 출처 정보를 담은 DataSourceResponse DTO
     */
    @Override
    @Transactional(readOnly = true)
    public DataSourceResponse findDataSource(Long dataSourceId) {
        DataSource dataSource = dataSourceRepositoryPort.findDataSourceById(dataSourceId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE));
        return dataSourceDtoMapper.toResponseDto(dataSource);
    }
}
