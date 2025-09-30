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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VisitSourceQueryServiceTest {

    @Mock
    private VisitSourcePort visitSourcePort;

    @Mock
    private VisitSourceDtoMapper visitSourceDtoMapper;

    @InjectMocks
    private VisitSourceQueryService service;

    @Test
    @DisplayName("전체 방문경로 조회 성공")
    void findAllVisitSourcesSuccess() {
        // given
        List<VisitSource> domainList = List.of(
                new VisitSource(1L, "v1", "l1"),
                new VisitSource(2L, "v2", "l2")
        );
        AllVisitSourcesResponse mapped = new AllVisitSourcesResponse(
                List.of(
                        new VisitSourceResponse(1L, "v1", "l1"),
                        new VisitSourceResponse(2L, "v2", "l2")
                )
        );
        given(visitSourcePort.findAllVisitSources()).willReturn(domainList);
        given(visitSourceDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllVisitSourcesResponse result = service.findAllVisitSources();

        // then
        assertThat(result).isSameAs(mapped);
        then(visitSourcePort).should().findAllVisitSources();
        then(visitSourceDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("방문경로 단건 조회 성공")
    void findVisitSourceSuccess() {
        // given
        Long id = 10L;
        VisitSource domain = new VisitSource(id, "v", "l");
        VisitSourceResponse mapped = new VisitSourceResponse(id, "v", "l");
        given(visitSourcePort.findVisitSourceById(id)).willReturn(Optional.of(domain));
        given(visitSourceDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        VisitSourceResponse result = service.findVisitSource(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(visitSourcePort).should().findVisitSourceById(id);
        then(visitSourceDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("방문경로 단건 조회 실패 - 없을 때 예외 발생")
    void findVisitSourceFailWhenNotFound() {
        // given
        Long id = 999L;
        given(visitSourcePort.findVisitSourceById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.findVisitSource(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(visitSourcePort).should().findVisitSourceById(id);
        then(visitSourceDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("방문경로 라벨 조회 성공")
    void getLabelByIdSuccess() {
        // given
        Long id = 1L;
        given(visitSourcePort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(visitSourcePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("방문경로 라벨 조회 실패 - 없을 때 예외 발생")
    void getLabelByIdFailWhenNotFound() {
        // given
        Long id = 404L;
        given(visitSourcePort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.getLabelById(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(visitSourcePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("방문경로 검증 성공 - 존재할 때")
    void validateVisitSourceSuccess() {
        // given
        Long id = 1L;
        given(visitSourcePort.existsVisitSourceById(id)).willReturn(true);

        // when
        service.validateVisitSource(id);

        // then
        then(visitSourcePort).should().existsVisitSourceById(id);
    }

    @Test
    @DisplayName("방문경로 검증 실패 - 없을 때 예외 발생")
    void validateVisitSourceFailWhenNotFound() {
        // given
        Long id = 2L;
        given(visitSourcePort.existsVisitSourceById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.validateVisitSource(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(visitSourcePort).should().existsVisitSourceById(id);
    }

    @Test
    @DisplayName("방문경로 라벨 다건 조회 성공 및 빈 값 처리")
    void getLabelsByIdsSuccessAndEmptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(visitSourcePort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(visitSourcePort).should().getLabelsByIds(ids);
    }
}
