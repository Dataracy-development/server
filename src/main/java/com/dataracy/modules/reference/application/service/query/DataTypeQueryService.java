package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import com.dataracy.modules.reference.application.mapper.DataTypeDtoMapper;
import com.dataracy.modules.reference.application.port.in.datatype.FindAllDataTypesUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.FindDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.out.DataTypeRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataType;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataTypeQueryService implements
        FindAllDataTypesUseCase,
        FindDataTypeUseCase,
        ValidateDataTypeUseCase,
        GetDataTypeLabelFromIdUseCase
{
    private final DataTypeDtoMapper dataTypeDtoMapper;
    private final DataTypeRepositoryPort dataTypeRepositoryPort;

    /**
     * 모든 데이터 유형을 조회하여 AllDataTypesResponse DTO로 반환한다.
     *
     * @return 전체 데이터 유형 정보를 포함하는 AllDataTypesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllDataTypesResponse findAllDataTypes() {
        List<DataType> dataTypes = dataTypeRepositoryPort.findAllDataTypes();
        return dataTypeDtoMapper.toResponseDto(dataTypes);
    }

    /**
     * 주어진 ID에 해당하는 데이터 유형을 조회하여 DataTypeResponse DTO로 반환한다.
     * 데이터 유형이 존재하지 않으면 ReferenceException을 발생시킨다.
     *
     * @param dataTypeId 조회할 데이터 유형의 ID
     * @return 조회된 데이터 유형 정보를 담은 DataTypeResponse DTO
     * @throws ReferenceException 데이터 유형이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public DataTypeResponse findDataType(Long dataTypeId) {
        DataType dataType = dataTypeRepositoryPort.findDataTypeById(dataTypeId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_TYPE));
        return dataTypeDtoMapper.toResponseDto(dataType);
    }

    /**
     * 주어진 데이터 타입 ID가 존재하는지 검증합니다.
     *
     * 데이터 타입이 존재하지 않을 경우 {@code ReferenceException}을 발생시킵니다.
     *
     * @param dataTypeId 검증할 데이터 타입의 ID
     * @throws ReferenceException 데이터 타입이 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateDataType(Long dataTypeId) {
        Boolean isExist = dataTypeRepositoryPort.existsDataTypeById(dataTypeId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_TYPE);
        }
    }

    @Override
    public String getLabelById(Long dataTypeId) {
        Optional<String> label = dataTypeRepositoryPort.getLabelById(dataTypeId);
        if (label.isEmpty()) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_TYPE);
        }
        return label.get();
    }
}
