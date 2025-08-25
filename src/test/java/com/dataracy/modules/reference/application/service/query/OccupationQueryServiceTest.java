package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;
import com.dataracy.modules.reference.application.mapper.OccupationDtoMapper;
import com.dataracy.modules.reference.application.port.out.OccupationPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.Occupation;
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
class OccupationQueryServiceTest {
    @Mock OccupationPort occupationPort;
    @Mock OccupationDtoMapper occupationDtoMapper;

    @InjectMocks OccupationQueryService service;

    @Test
    @DisplayName("findAllOccupations: 성공 - 전체 목록 반환")
    void findAllOccupations_success() {
        // given
        List<Occupation> domainList = List.of(new Occupation(1L, "v1", "l1"), new Occupation(2L, "v2", "l2"));
        AllOccupationsResponse mapped = new AllOccupationsResponse(List.of(new OccupationResponse(1L, "v1", "l1"), new OccupationResponse(2L, "v2", "l2")));
        given(occupationPort.findAllOccupations()).willReturn(domainList);
        given(occupationDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllOccupationsResponse result = service.findAllOccupations();

        // then
        assertThat(result).isSameAs(mapped);
        then(occupationPort).should().findAllOccupations();
        then(occupationDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("findOccupation: 성공 - 단건 반환")
    void findOccupation_success() {
        // given
        Long id = 10L;
        Occupation domain = new Occupation(id, "v", "l");
        OccupationResponse mapped = new OccupationResponse(id, "v", "l");
        given(occupationPort.findOccupationById(id)).willReturn(Optional.of(domain));
        given(occupationDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        OccupationResponse result = service.findOccupation(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(occupationPort).should().findOccupationById(id);
        then(occupationDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("findOccupation: 실패 - 존재하지 않으면 ReferenceException")
    void findOccupation_notFound_throws() {
        // given
        Long id = 999L;
        given(occupationPort.findOccupationById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.findOccupation(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(occupationPort).should().findOccupationById(id);
        then(occupationDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getLabelById: 성공 - 라벨 반환")
    void getLabelById_success() {
        // given
        Long id = 1L;
        given(occupationPort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(occupationPort).should().getLabelById(id);
    }

    @Test
    @DisplayName("getLabelById: 실패 - 없으면 ReferenceException")
    void getLabelById_notFound_throws() {
        // given
        Long id = 404L;
        given(occupationPort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.getLabelById(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(occupationPort).should().getLabelById(id);
    }

    @Test
    @DisplayName("validateOccupation: 성공 - 존재하면 예외 없음")
    void validateOccupation_success() {
        // given
        Long id = 1L;
        given(occupationPort.existsOccupationById(id)).willReturn(true);

        // when
        service.validateOccupation(id);

        // then
        then(occupationPort).should().existsOccupationById(id);
    }

    @Test
    @DisplayName("validateOccupation: 실패 - 존재하지 않으면 ReferenceException")
    void validateOccupation_notFound_throws() {
        // given
        Long id = 2L;
        given(occupationPort.existsOccupationById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.validateOccupation(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(occupationPort).should().existsOccupationById(id);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - 비어있으면 빈 맵, 값 있으면 위임 및 반환")
    void getLabelsByIds_success_and_emptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(occupationPort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(occupationPort).should().getLabelsByIds(ids);
    }
}
