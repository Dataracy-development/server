package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.application.mapper.DataSourceDtoMapper;
import com.dataracy.modules.reference.application.port.in.datasource.FindAllDataSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.FindDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.out.DataSourcePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataSource;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataSourceQueryService implements
        FindAllDataSourcesUseCase,
        FindDataSourceUseCase,
        ValidateDataSourceUseCase,
        GetDataSourceLabelFromIdUseCase
{
    private final DataSourceDtoMapper dataSourceDtoMapper;
    private final DataSourcePort dataSourcePort;

    // Use Case 상수 정의
    private static final String FIND_ALL_DATA_SOURCES_USE_CASE = "FindAllDataSourcesUseCase";
    private static final String FIND_DATA_SOURCE_USE_CASE = "FindDataSourceUseCase";
    private static final String VALIDATE_DATA_SOURCE_USE_CASE = "ValidateDataSourceUseCase";
    private static final String GET_DATA_SOURCE_LABEL_FROM_ID_USE_CASE = "GetDataSourceLabelFromIdUseCase";
    
    // 메시지 상수 정의
    private static final String DATA_SOURCE_NOT_FOUND_MESSAGE = "해당 데이터 출처가 존재하지 않습니다. dataSourceId=";
    private static final String DATA_SOURCE_NOT_FOUND_MESSAGE_2 = "해당 데이터 소스가 존재하지 않습니다. dataSourceId=";

    /**
     * 모든 데이터 소스의 전체 목록을 조회하여 DTO로 반환한다.
     *
     * @return 전체 데이터 소스 정보를 포함하는 AllDataSourcesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllDataSourcesResponse findAllDataSources() {
        Instant startTime = LoggerFactory.service().logStart(FIND_ALL_DATA_SOURCES_USE_CASE, "모든 데이터 출처 정보 조회 서비스 시작");
        List<DataSource> dataSources = dataSourcePort.findAllDataSources();
        AllDataSourcesResponse allDataSourcesResponse = dataSourceDtoMapper.toResponseDto(dataSources);
        LoggerFactory.service().logSuccess(FIND_ALL_DATA_SOURCES_USE_CASE, "모든 데이터 출처 정보 조회 서비스 종료", startTime);
        return allDataSourcesResponse;
    }

    /**
     * 주어진 ID에 해당하는 데이터 출처 정보를 조회하여 DataSourceResponse DTO로 반환한다.
     * 데이터 출처가 존재하지 않을 경우 ReferenceException을 발생시킨다.
     *
     * @param dataSourceId 조회할 데이터 출처의 ID
     * @return 조회된 데이터 출처 정보를 담은 DataSourceResponse DTO
     * @throws ReferenceException 데이터 출처가 존재하지 않을 경우
     */
    @Override
    @Transactional(readOnly = true)
    public DataSourceResponse findDataSource(Long dataSourceId) {
        Instant startTime = LoggerFactory.service().logStart(FIND_DATA_SOURCE_USE_CASE, "주어진 ID로 데이터 출처 조회 서비스 시작 dataSourceId=" + dataSourceId);
        DataSource dataSource = dataSourcePort.findDataSourceById(dataSourceId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(FIND_DATA_SOURCE_USE_CASE, DATA_SOURCE_NOT_FOUND_MESSAGE + dataSourceId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE);
                });
        DataSourceResponse dataSourceResponse = dataSourceDtoMapper.toResponseDto(dataSource);
        LoggerFactory.service().logSuccess(FIND_DATA_SOURCE_USE_CASE, "주어진 ID로 데이터 출처 조회 서비스 종료 dataSourceId=" + dataSourceId, startTime);
        return dataSourceResponse;
    }

    /**
     * 주어진 데이터 소스 ID가 존재하는지 검증합니다.
     * 데이터 소스가 존재하지 않을 경우 ReferenceException을 발생시킵니다.
     *
     * @param dataSourceId 존재 여부를 확인할 데이터 소스의 ID
     * @throws ReferenceException 데이터 소스를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDataSource(Long dataSourceId) {
        Instant startTime = LoggerFactory.service().logStart(VALIDATE_DATA_SOURCE_USE_CASE, "주어진 ID에 해당하는 데이터 소스가 존재하는지 확인 서비스 시작 dataSourceId=" + dataSourceId);
        boolean isExist = dataSourcePort.existsDataSourceById(dataSourceId);
        if (!isExist) {
            LoggerFactory.service().logWarning(VALIDATE_DATA_SOURCE_USE_CASE, DATA_SOURCE_NOT_FOUND_MESSAGE_2 + dataSourceId);
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE);
        }
        LoggerFactory.service().logSuccess(VALIDATE_DATA_SOURCE_USE_CASE, "주어진 ID에 해당하는 데이터 소스가 존재하는지 확인 서비스 종료 dataSourceId=" + dataSourceId, startTime);
    }

    /**
     * 주어진 데이터 소스 ID로 해당 데이터 소스의 라벨을 반환합니다.
     * 데이터 소스가 존재하지 않을 경우 ReferenceException이 발생합니다.
     *
     * @param dataSourceId 라벨을 조회할 데이터 소스의 ID
     * @return 데이터 소스의 라벨 문자열
     * @throws ReferenceException 데이터 소스를 찾을 수 없는 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long dataSourceId) {
        Instant startTime = LoggerFactory.service().logStart(GET_DATA_SOURCE_LABEL_FROM_ID_USE_CASE, "주어진 데이터 소스 ID에 해당하는 라벨을 조회 서비스 시작 dataSourceId=" + dataSourceId);
        String label = dataSourcePort.getLabelById(dataSourceId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(GET_DATA_SOURCE_LABEL_FROM_ID_USE_CASE, DATA_SOURCE_NOT_FOUND_MESSAGE_2 + dataSourceId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE);
                });
        LoggerFactory.service().logSuccess(GET_DATA_SOURCE_LABEL_FROM_ID_USE_CASE, "주어진 데이터 소스 ID에 해당하는 라벨을 조회 서비스 종료 dataSourceId=" + dataSourceId, startTime);
        return label;
    }

    /**
     * 주어진 데이터 소스 ID 목록에 대해 각 ID와 해당 라벨을 매핑한 맵을 반환합니다.
     *
     * @param dataSourceIds 라벨을 조회할 데이터 소스 ID 목록
     * @return 각 데이터 소스 ID와 라벨의 매핑 맵. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> dataSourceIds) {
        Instant startTime = LoggerFactory.service().logStart(GET_DATA_SOURCE_LABEL_FROM_ID_USE_CASE, "데이터 소스 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작");
        if (dataSourceIds == null || dataSourceIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> labels = dataSourcePort.getLabelsByIds(dataSourceIds);
        LoggerFactory.service().logSuccess(GET_DATA_SOURCE_LABEL_FROM_ID_USE_CASE, "데이터 소스 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료", startTime);
        return labels;
    }
}
