package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;
import com.dataracy.modules.reference.application.mapper.AnalysisPurposeDtoMapper;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
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
class AnalysisPurposeQueryServiceTest {

    @Mock AnalysisPurposePort analysisPurposePort;
    @Mock AnalysisPurposeDtoMapper analysisPurposeDtoMapper;

    @InjectMocks AnalysisPurposeQueryService service;

    @Test
    @DisplayName("findAllAnalysisPurposes: 성공 - 전체 목록 반환")
    void findAllAnalysisPurposes_success() {
        // given
        List<AnalysisPurpose> domainList = List.of(
                new AnalysisPurpose(1L, "v1", "l1"),
                new AnalysisPurpose(2L, "v2", "l2")
        );
        AllAnalysisPurposesResponse mapped = new AllAnalysisPurposesResponse(
                List.of(
                        new AnalysisPurposeResponse(1L, "v1", "l1"),
                        new AnalysisPurposeResponse(2L, "v2", "l2")
                )
        );
        given(analysisPurposePort.findAllAnalysisPurposes()).willReturn(domainList);
        given(analysisPurposeDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllAnalysisPurposesResponse result = service.findAllAnalysisPurposes();

        // then
        assertThat(result).isSameAs(mapped);
        then(analysisPurposePort).should().findAllAnalysisPurposes();
        then(analysisPurposeDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("findAnalysisPurpose: 성공 - 단건 반환")
    void findAnalysisPurpose_success() {
        // given
        Long id = 10L;
        AnalysisPurpose domain = new AnalysisPurpose(id, "v", "l");
        AnalysisPurposeResponse mapped = new AnalysisPurposeResponse(id, "v", "l");
        given(analysisPurposePort.findAnalysisPurposeById(id)).willReturn(Optional.of(domain));
        given(analysisPurposeDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        AnalysisPurposeResponse result = service.findAnalysisPurpose(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(analysisPurposePort).should().findAnalysisPurposeById(id);
        then(analysisPurposeDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("findAnalysisPurpose: 실패 - 존재하지 않으면 ReferenceException")
    void findAnalysisPurpose_notFound_throws() {
        // given
        Long id = 999L;
        given(analysisPurposePort.findAnalysisPurposeById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.findAnalysisPurpose(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(analysisPurposePort).should().findAnalysisPurposeById(id);
        then(analysisPurposeDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getLabelById: 성공 - 라벨 반환")
    void getLabelById_success() {
        // given
        Long id = 1L;
        given(analysisPurposePort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(analysisPurposePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("getLabelById: 실패 - 없으면 ReferenceException")
    void getLabelById_notFound_throws() {
        // given
        Long id = 404L;
        given(analysisPurposePort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.getLabelById(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(analysisPurposePort).should().getLabelById(id);
    }

    @Test
    @DisplayName("validateAnalysisPurpose: 성공 - 존재하면 예외 없음")
    void validateAnalysisPurpose_success() {
        // given
        Long id = 1L;
        given(analysisPurposePort.existsAnalysisPurposeById(id)).willReturn(true);

        // when
        service.validateAnalysisPurpose(id);

        // then
        then(analysisPurposePort).should().existsAnalysisPurposeById(id);
    }

    @Test
    @DisplayName("validateAnalysisPurpose: 실패 - 존재하지 않으면 ReferenceException")
    void validateAnalysisPurpose_notFound_throws() {
        // given
        Long id = 2L;
        given(analysisPurposePort.existsAnalysisPurposeById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.validateAnalysisPurpose(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(analysisPurposePort).should().existsAnalysisPurposeById(id);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - 비어있으면 빈 맵, 값 있으면 위임 및 반환")
    void getLabelsByIds_success_and_emptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(analysisPurposePort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(analysisPurposePort).should().getLabelsByIds(ids);
    }
}
