package com.dataracy.modules.reference.adapter.web.api.datatype;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.DataTypeWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.port.in.datatype.FindAllDataTypesUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DataTypeController implements DataTypeApi {
    private final DataTypeWebMapper dataTypeWebMapper;
    private final FindAllDataTypesUseCase findAllDataTypesUseCase;

    /**
     * 데이터베이스에 저장된 모든 데이터 유형 목록을 조회하여 성공 응답으로 반환합니다.
     *
     * @return 전체 데이터 유형 정보를 포함하는 성공 응답의 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<AllDataTypesWebResponse>> findAllDataTypes (
    ) {
        AllDataTypesResponse allDataTypesResponse = findAllDataTypesUseCase.findAllDataTypes();
        AllDataTypesWebResponse allDataTypesWebResponse = dataTypeWebMapper.toWebDto(allDataTypesResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_DATA_TYPE_LIST, allDataTypesWebResponse));
    }
}
