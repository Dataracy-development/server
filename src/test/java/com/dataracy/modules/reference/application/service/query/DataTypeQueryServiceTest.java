package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import com.dataracy.modules.reference.application.mapper.DataTypeDtoMapper;
import com.dataracy.modules.reference.application.port.out.DataTypePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DataTypeQueryServiceTest {

    @Mock
    private DataTypePort dataTypePort;

    @Mock
    private DataTypeDtoMapper dataTypeDtoMapper;

    @InjectMocks
    private DataTypeQueryService service;

    @Test
    @DisplayName("데이터 타입 전체 조회 성공")
    void findAllDataTypesSuccess() {
        // given
        List<DataType> domainList = List.of(
                new DataType(1L, "v1", "l1"),
                new DataType(2L, "v2", "l2")
        );
        AllDataTypesResponse mapped = new AllDataTypesResponse(
                List.of(
                        new DataTypeResponse(1L, "v1", "l1"),
                        new DataTypeResponse(2L, "v2", "l2")
                )
        );
        given(dataTypePort.findAllDataTypes()).willReturn(domainList);
        given(dataTypeDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllDataTypesResponse result = service.findAllDataTypes();

        // then
        assertThat(result).isSameAs(mapped);
        then(dataTypePort).should().findAllDataTypes();
        then(dataTypeDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("데이터 타입 단건 조회 성공")
    void findDataTypeSuccess() {
        // given
        Long id = 10L;
        DataType domain = new DataType(id, "v", "l");
        DataTypeResponse mapped = new DataTypeResponse(id, "v", "l");
        given(dataTypePort.findDataTypeById(id)).willReturn(Optional.of(domain));
        given(dataTypeDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        DataTypeResponse result = service.findDataType(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(dataTypePort).should().findDataTypeById(id);
        then(dataTypeDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("데이터 타입 단건 조회 실패 - 없을 때 예외 발생")
    void findDataTypeFailWhenNotFound() {
        // given
        Long id = 999L;
        given(dataTypePort.findDataTypeById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.findDataType(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(dataTypePort).should().findDataTypeById(id);
        then(dataTypeDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("데이터 타입 라벨 조회 성공")
    void getLabelByIdSuccess() {
        // given
        Long id = 1L;
        given(dataTypePort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(dataTypePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("데이터 타입 라벨 조회 실패 - 없을 때 예외 발생")
    void getLabelByIdFailWhenNotFound() {
        // given
        Long id = 404L;
        given(dataTypePort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.getLabelById(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(dataTypePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("데이터 타입 검증 성공 - 존재할 때")
    void validateDataTypeSuccess() {
        // given
        Long id = 1L;
        given(dataTypePort.existsDataTypeById(id)).willReturn(true);

        // when
        service.validateDataType(id);

        // then
        then(dataTypePort).should().existsDataTypeById(id);
    }

    @Test
    @DisplayName("데이터 타입 검증 실패 - 없을 때 예외 발생")
    void validateDataTypeFailWhenNotFound() {
        // given
        Long id = 2L;
        given(dataTypePort.existsDataTypeById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.validateDataType(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(dataTypePort).should().existsDataTypeById(id);
    }

    @Test
    @DisplayName("데이터 타입 라벨 다건 조회 성공 및 빈 값 처리")
    void getLabelsByIdsSuccessAndEmptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(dataTypePort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(dataTypePort).should().getLabelsByIds(ids);
    }
}
