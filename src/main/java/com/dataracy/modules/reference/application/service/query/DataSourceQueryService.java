package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.application.mapper.DataSourceDtoMapper;
import com.dataracy.modules.reference.application.port.in.datasource.FindAllDataSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.FindDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
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
        FindDataSourceUseCase,
        ValidateDataSourceUseCase
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
    public AllDataSourcesResponse findAllDataSources() {
        List<DataSource> dataSources = dataSourceRepositoryPort.findAllDataSources();
        return dataSourceDtoMapper.toResponseDto(dataSources);
    }

    /**
     * 주어진 ID에 해당하는 데이터 출처 정보를 조회하여 DataSourceResponse DTO로 반환한다.
     *
     * 데이터 출처가 존재하지 않으면 ReferenceException이 발생한다.
     *
     * @param dataSourceId 조회할 데이터 출처의 ID
     * @return 조회된 데이터 출처 정보를 담은 DataSourceResponse DTO
     * @throws ReferenceException 데이터 출처가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public DataSourceResponse findDataSource(Long dataSourceId) {
        DataSource dataSource = dataSourceRepositoryPort.findDataSourceById(dataSourceId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE));
        return dataSourceDtoMapper.toResponseDto(dataSource);
    }

    /**
     * 주어진 데이터 소스 ID가 존재하는지 검증합니다.
     *
     * 데이터 소스가 존재하지 않을 경우 {@code ReferenceException}을 발생시킵니다.
     *
     * @param dataSourceId 검증할 데이터 소스의 ID
     * @throws ReferenceException 데이터 소스를 찾을 수 없는 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDataSource(Long dataSourceId) {
        Boolean isExist = dataSourceRepositoryPort.existsDataSourceById(dataSourceId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE);
        }
    }
}
