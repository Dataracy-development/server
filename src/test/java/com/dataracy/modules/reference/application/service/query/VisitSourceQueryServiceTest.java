package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;
import com.dataracy.modules.reference.application.mapper.VisitSourceDtoMapper;
import com.dataracy.modules.reference.application.port.out.VisitSourcePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.VisitSource;
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
class VisitSourceQueryServiceTest {

    @Mock VisitSourcePort visitsourcePort;
    @Mock VisitSourceDtoMapper visitsourceDtoMapper;

    @InjectMocks VisitSourceQueryService service;

    @Test
    @DisplayName("findAllVisitSources: 성공 - 전체 목록 반환")
    void findAllVisitSources_success() {
        // given
        List<VisitSource> domainList = List.of(new VisitSource(1L, "v1", "l1"), new VisitSource(2L, "v2", "l2"));
        AllVisitSourcesResponse mapped = new AllVisitSourcesResponse(List.of(new VisitSourceResponse(1L, "v1", "l1"), new VisitSourceResponse(2L, "v2", "l2")));
        given(visitsourcePort.findAllVisitSources()).willReturn(domainList);
        given(visitsourceDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllVisitSourcesResponse result = service.findAllVisitSources();

        // then
        assertThat(result).isSameAs(mapped);
        then(visitsourcePort).should().findAllVisitSources();
        then(visitsourceDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("findVisitSource: 성공 - 단건 반환")
    void findVisitSource_success() {
        // given
        Long id = 10L;
        VisitSource domain = new VisitSource(id, "v", "l");
        VisitSourceResponse mapped = new VisitSourceResponse(id, "v", "l");
        given(visitsourcePort.findVisitSourceById(id)).willReturn(Optional.of(domain));
        given(visitsourceDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        VisitSourceResponse result = service.findVisitSource(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(visitsourcePort).should().findVisitSourceById(id);
        then(visitsourceDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("findVisitSource: 실패 - 존재하지 않으면 ReferenceException")
    void findVisitSource_notFound_throws() {
        // given
        Long id = 999L;
        given(visitsourcePort.findVisitSourceById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.findVisitSource(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(visitsourcePort).should().findVisitSourceById(id);
        then(visitsourceDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getLabelById: 성공 - 라벨 반환")
    void getLabelById_success() {
        // given
        Long id = 1L;
        given(visitsourcePort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(visitsourcePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("getLabelById: 실패 - 없으면 ReferenceException")
    void getLabelById_notFound_throws() {
        // given
        Long id = 404L;
        given(visitsourcePort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.getLabelById(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(visitsourcePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("validateVisitSource: 성공 - 존재하면 예외 없음")
    void validateVisitSource_success() {
        // given
        Long id = 1L;
        given(visitsourcePort.existsVisitSourceById(id)).willReturn(true);

        // when
        service.validateVisitSource(id);

        // then
        then(visitsourcePort).should().existsVisitSourceById(id);
    }

    @Test
    @DisplayName("validateVisitSource: 실패 - 존재하지 않으면 ReferenceException")
    void validateVisitSource_notFound_throws() {
        // given
        Long id = 2L;
        given(visitsourcePort.existsVisitSourceById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.validateVisitSource(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(visitsourcePort).should().existsVisitSourceById(id);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - 비어있으면 빈 맵, 값 있으면 위임 및 반환")
    void getLabelsByIds_success_and_emptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(visitsourcePort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(visitsourcePort).should().getLabelsByIds(ids);
    }
}
