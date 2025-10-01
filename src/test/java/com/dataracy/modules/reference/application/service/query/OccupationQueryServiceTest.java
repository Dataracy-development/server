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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OccupationQueryServiceTest {

    @Mock
    private OccupationPort occupationPort;

    @Mock
    private OccupationDtoMapper occupationDtoMapper;

    @InjectMocks
    private OccupationQueryService service;

    @Test
    @DisplayName("전체 직업 조회 성공")
    void findAllOccupationsSuccess() {
        // given
        List<Occupation> domainList = List.of(
                new Occupation(1L, "v1", "l1"),
                new Occupation(2L, "v2", "l2")
        );
        AllOccupationsResponse mapped = new AllOccupationsResponse(
                List.of(
                        new OccupationResponse(1L, "v1", "l1"),
                        new OccupationResponse(2L, "v2", "l2")
                )
        );
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
    @DisplayName("직업 단건 조회 성공")
    void findOccupationSuccess() {
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
    @DisplayName("직업 단건 조회 실패 - 없을 때 예외 발생")
    void findOccupationFailWhenNotFound() {
        // given
        Long id = 999L;
        given(occupationPort.findOccupationById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.findOccupation(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(occupationPort).should().findOccupationById(id);
        then(occupationDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("직업 라벨 조회 성공")
    void getLabelByIdSuccess() {
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
    @DisplayName("직업 라벨 조회 실패 - 없을 때 예외 발생")
    void getLabelByIdFailWhenNotFound() {
        // given
        Long id = 404L;
        given(occupationPort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.getLabelById(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(occupationPort).should().getLabelById(id);
    }

    @Test
    @DisplayName("직업 검증 성공 - 존재할 때")
    void validateOccupationSuccess() {
        // given
        Long id = 1L;
        given(occupationPort.existsOccupationById(id)).willReturn(true);

        // when
        service.validateOccupation(id);

        // then
        then(occupationPort).should().existsOccupationById(id);
    }

    @Test
    @DisplayName("직업 검증 실패 - 없을 때 예외 발생")
    void validateOccupationFailWhenNotFound() {
        // given
        Long id = 2L;
        given(occupationPort.existsOccupationById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.validateOccupation(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(occupationPort).should().existsOccupationById(id);
    }

    @Test
    @DisplayName("직업 라벨 다건 조회 성공 및 빈 값 처리")
    void getLabelsByIdsSuccessAndEmptyHandling() {
        // given - empty/null
        assertAll(
                () -> assertThat(service.getLabelsByIds(null)).isEmpty(),
                () -> assertThat(service.getLabelsByIds(List.of())).isEmpty()
        );

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
