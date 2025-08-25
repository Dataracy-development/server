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

    @Mock DataTypePort datatypePort;
    @Mock DataTypeDtoMapper datatypeDtoMapper;

    @InjectMocks DataTypeQueryService service;

    @Test
    @DisplayName("findAllDataTypes: 성공 - 전체 목록 반환")
    void findAllDataTypes_success() {
        // given
        List<DataType> domainList = List.of(new DataType(1L, "v1", "l1"), new DataType(2L, "v2", "l2"));
        AllDataTypesResponse mapped = new AllDataTypesResponse(List.of(new DataTypeResponse(1L, "v1", "l1"), new DataTypeResponse(2L, "v2", "l2")));
        given(datatypePort.findAllDataTypes()).willReturn(domainList);
        given(datatypeDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllDataTypesResponse result = service.findAllDataTypes();

        // then
        assertThat(result).isSameAs(mapped);
        then(datatypePort).should().findAllDataTypes();
        then(datatypeDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("findDataType: 성공 - 단건 반환")
    void findDataType_success() {
        // given
        Long id = 10L;
        DataType domain = new DataType(id, "v", "l");
        DataTypeResponse mapped = new DataTypeResponse(id, "v", "l");
        given(datatypePort.findDataTypeById(id)).willReturn(Optional.of(domain));
        given(datatypeDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        DataTypeResponse result = service.findDataType(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(datatypePort).should().findDataTypeById(id);
        then(datatypeDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("findDataType: 실패 - 존재하지 않으면 ReferenceException")
    void findDataType_notFound_throws() {
        // given
        Long id = 999L;
        given(datatypePort.findDataTypeById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.findDataType(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(datatypePort).should().findDataTypeById(id);
        then(datatypeDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getLabelById: 성공 - 라벨 반환")
    void getLabelById_success() {
        // given
        Long id = 1L;
        given(datatypePort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(datatypePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("getLabelById: 실패 - 없으면 ReferenceException")
    void getLabelById_notFound_throws() {
        // given
        Long id = 404L;
        given(datatypePort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.getLabelById(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(datatypePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("validateDataType: 성공 - 존재하면 예외 없음")
    void validateDataType_success() {
        // given
        Long id = 1L;
        given(datatypePort.existsDataTypeById(id)).willReturn(true);

        // when
        service.validateDataType(id);

        // then
        then(datatypePort).should().existsDataTypeById(id);
    }

    @Test
    @DisplayName("validateDataType: 실패 - 존재하지 않으면 ReferenceException")
    void validateDataType_notFound_throws() {
        // given
        Long id = 2L;
        given(datatypePort.existsDataTypeById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.validateDataType(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(datatypePort).should().existsDataTypeById(id);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - 비어있으면 빈 맵, 값 있으면 위임 및 반환")
    void getLabelsByIds_success_and_emptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(datatypePort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(datatypePort).should().getLabelsByIds(ids);
    }
}
