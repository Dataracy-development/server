package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import com.dataracy.modules.reference.application.mapper.DataTypeDtoMapper;
import com.dataracy.modules.reference.application.port.in.datatype.FindAllDataTypesUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.FindDataTypeUseCase;
import com.dataracy.modules.reference.application.port.out.DataTypeRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataType;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataTypeQueryService implements
        FindAllDataTypesUseCase,
        FindDataTypeUseCase
{
    private final DataTypeDtoMapper dataTypeDtoMapper;
    private final DataTypeRepositoryPort dataTypeRepositoryPort;

    /**
     * 모든 데이터 유형의 목록을 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 데이터 유형 정보를 담은 AllDataTypesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllDataTypesResponse findAllDataTypes() {
        List<DataType> dataTypes = dataTypeRepositoryPort.findAllDataTypes();
        return dataTypeDtoMapper.toResponseDto(dataTypes);
    }

    /**
     * 주어진 ID로 데이터 유형을 조회하여 응답 DTO로 반환한다.
     * 데이터 유형이 존재하지 않을 경우 ReferenceException을 발생시킨다.
     *
     * @param dataTypeId 조회할 데이터 유형의 ID
     * @return 데이터 유형 정보를 담은 DataTypeResponse DTO
     */
    @Override
    @Transactional(readOnly = true)
    public DataTypeResponse findDataType(Long dataTypeId) {
        DataType dataType = dataTypeRepositoryPort.findDataTypeById(dataTypeId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_TYPE));
        return dataTypeDtoMapper.toResponseDto(dataType);
    }
}
