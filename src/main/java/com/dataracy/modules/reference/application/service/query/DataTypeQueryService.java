package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import com.dataracy.modules.reference.application.mapper.DataTypeDtoMapper;
import com.dataracy.modules.reference.application.port.in.datatype.FindAllDataTypesUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.FindDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.out.DataTypePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataType;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataTypeQueryService implements
        FindAllDataTypesUseCase,
        FindDataTypeUseCase,
        ValidateDataTypeUseCase,
        GetDataTypeLabelFromIdUseCase
{
    private final DataTypeDtoMapper dataTypeDtoMapper;
    private final DataTypePort dataTypePort;

    /**
     * 모든 데이터 유형 정보를 조회하여 AllDataTypesResponse DTO로 반환한다.
     *
     * @return 전체 데이터 유형 정보를 포함하는 AllDataTypesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllDataTypesResponse findAllDataTypes() {
        Instant startTime = LoggerFactory.service().logStart("FindAllDataTypesUseCase", "모든 데이터 유형 정보 조회 서비스 시작");
        List<DataType> dataTypes = dataTypePort.findAllDataTypes();
        AllDataTypesResponse allDataTypesResponse = dataTypeDtoMapper.toResponseDto(dataTypes);
        LoggerFactory.service().logSuccess("FindAllDataTypesUseCase", "모든 데이터 유형 정보 조회 서비스 종료", startTime);
        return allDataTypesResponse;
    }

    /**
     * 주어진 ID에 해당하는 데이터 유형을 조회하여 DataTypeResponse DTO로 반환한다.
     * 데이터 유형이 존재하지 않을 경우 ReferenceException이 발생한다.
     *
     * @param dataTypeId 조회할 데이터 유형의 ID
     * @return 조회된 데이터 유형 정보를 담은 DataTypeResponse DTO
     * @throws ReferenceException 데이터 유형이 존재하지 않을 경우
     */
    @Override
    @Transactional(readOnly = true)
    public DataTypeResponse findDataType(Long dataTypeId) {
        Instant startTime = LoggerFactory.service().logStart("FindDataTypeUseCase", "주어진 ID로 데이터 유형 조회 서비스 시작 dataTypeId=" + dataTypeId);
        DataType dataType = dataTypePort.findDataTypeById(dataTypeId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindDataTypeUseCase", "해당 데이터 유형이 존재하지 않습니다. dataTypeId=" + dataTypeId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_TYPE);
                });
        DataTypeResponse dataTypeResponse = dataTypeDtoMapper.toResponseDto(dataType);
        LoggerFactory.service().logSuccess("FindDataTypeUseCase", "주어진 ID로 데이터 유형 조회 서비스 종료 dataTypeId=" + dataTypeId, startTime);
        return dataTypeResponse;
    }

    /**
     * 주어진 데이터 타입 ID의 존재 여부를 검증합니다.
     * 데이터 타입이 존재하지 않을 경우 ReferenceException을 발생시킵니다.
     *
     * @param dataTypeId 존재 여부를 확인할 데이터 타입의 ID
     * @throws ReferenceException 데이터 타입이 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDataType(Long dataTypeId) {
        Instant startTime = LoggerFactory.service().logStart("ValidateDataTypeUseCase", "주어진 ID에 해당하는 데이터 유형이 존재하는지 확인 서비스 시작 dataTypeId=" + dataTypeId);
        Boolean isExist = dataTypePort.existsDataTypeById(dataTypeId);
        if (!isExist) {
            LoggerFactory.service().logWarning("ValidateDataTypeUseCase", "해당 데이터 유형이 존재하지 않습니다. dataTypeId=" + dataTypeId);
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_TYPE);
        }
        LoggerFactory.service().logSuccess("ValidateDataTypeUseCase", "주어진 ID에 해당하는 데이터 유형이 존재하는지 확인 서비스 종료 dataTypeId=" + dataTypeId, startTime);
    }

    /**
     * 주어진 데이터 타입 ID에 해당하는 라벨을 반환합니다.
     * 데이터 타입이 존재하지 않으면 ReferenceException이 발생합니다.
     *
     * @param dataTypeId 라벨을 조회할 데이터 타입의 ID
     * @return 해당 데이터 타입의 라벨 문자열
     * @throws ReferenceException 데이터 타입을 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long dataTypeId) {
        Instant startTime = LoggerFactory.service().logStart("GetDataTypeLabelFromIdUseCase", "주어진 데이터 유형 ID에 해당하는 라벨을 조회 서비스 시작 dataTypeId=" + dataTypeId);
        String label = dataTypePort.getLabelById(dataTypeId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("GetDataTypeLabelFromIdUseCase", "해당 데이터 유형이 존재하지 않습니다. dataTypeId=" + dataTypeId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_TYPE);
                });
        LoggerFactory.service().logSuccess("GetDataTypeLabelFromIdUseCase", "주어진 데이터 유형 ID에 해당하는 라벨을 조회 서비스 종료 dataTypeId=" + dataTypeId, startTime);
        return label;
    }

    /**
     * 데이터 타입 ID 목록에 대해 각 ID에 해당하는 라벨을 조회하여 반환합니다.
     *
     * @param dataTypeIds 라벨을 조회할 데이터 타입 ID 목록
     * @return 각 데이터 타입 ID와 해당 라벨의 매핑. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> dataTypeIds) {
        Instant startTime = LoggerFactory.service().logStart("GetDataTypeLabelFromIdUseCase", "데이터 유형 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작");
        if (dataTypeIds == null || dataTypeIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> labels = dataTypePort.getLabelsByIds(dataTypeIds);
        LoggerFactory.service().logSuccess("GetDataTypeLabelFromIdUseCase", "데이터 유형 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료", startTime);
        return labels;
    }
}
