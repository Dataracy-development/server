package com.dataracy.modules.reference.application.service.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.application.mapper.DataSourceDtoMapper;
import com.dataracy.modules.reference.application.port.out.DataSourcePort;
import com.dataracy.modules.reference.application.service.query.DataSourceQueryService;
import com.dataracy.modules.reference.application.port.in.datasource.FindAllDataSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.FindDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DataSourceQueryServiceTest {

    @Mock DataSourcePort datasourcePort;
    @Mock DataSourceDtoMapper datasourceDtoMapper;

    @InjectMocks DataSourceQueryService service;

    @Test
    @DisplayName("findAllDataSources: 성공 - 전체 목록 반환")
    void findAllDataSources_success() {
        // given
        List<DataSource> domainList = List.of(new DataSource(1L, "v1", "l1"), new DataSource(2L, "v2", "l2"));
        AllDataSourcesResponse mapped = new AllDataSourcesResponse(List.of(new DataSourceResponse(1L, "v1", "l1"), new DataSourceResponse(2L, "v2", "l2")));
        given(datasourcePort.findAllDataSources()).willReturn(domainList);
        given(datasourceDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllDataSourcesResponse result = service.findAllDataSources();

        // then
        assertThat(result).isSameAs(mapped);
        then(datasourcePort).should().findAllDataSources();
        then(datasourceDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("findDataSource: 성공 - 단건 반환")
    void findDataSource_success() {
        // given
        Long id = 10L;
        DataSource domain = new DataSource(id, "v", "l");
        DataSourceResponse mapped = new DataSourceResponse(id, "v", "l");
        given(datasourcePort.findDataSourceById(id)).willReturn(Optional.of(domain));
        given(datasourceDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        DataSourceResponse result = service.findDataSource(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(datasourcePort).should().findDataSourceById(id);
        then(datasourceDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("findDataSource: 실패 - 존재하지 않으면 ReferenceException")
    void findDataSource_notFound_throws() {
        // given
        Long id = 999L;
        given(datasourcePort.findDataSourceById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.findDataSource(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(datasourcePort).should().findDataSourceById(id);
        then(datasourceDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getLabelById: 성공 - 라벨 반환")
    void getLabelById_success() {
        // given
        Long id = 1L;
        given(datasourcePort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(datasourcePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("getLabelById: 실패 - 없으면 ReferenceException")
    void getLabelById_notFound_throws() {
        // given
        Long id = 404L;
        given(datasourcePort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.getLabelById(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(datasourcePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("validateDataSource: 성공 - 존재하면 예외 없음")
    void validateDataSource_success() {
        // given
        Long id = 1L;
        given(datasourcePort.existsDataSourceById(id)).willReturn(true);

        // when
        service.validateDataSource(id);

        // then
        then(datasourcePort).should().existsDataSourceById(id);
    }

    @Test
    @DisplayName("validateDataSource: 실패 - 존재하지 않으면 ReferenceException")
    void validateDataSource_notFound_throws() {
        // given
        Long id = 2L;
        given(datasourcePort.existsDataSourceById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.validateDataSource(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(datasourcePort).should().existsDataSourceById(id);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - 비어있으면 빈 맵, 값 있으면 위임 및 반환")
    void getLabelsByIds_success_and_emptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(datasourcePort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(datasourcePort).should().getLabelsByIds(ids);
    }
}
