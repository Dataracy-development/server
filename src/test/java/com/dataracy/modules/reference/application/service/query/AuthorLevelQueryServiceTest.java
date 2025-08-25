package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;
import com.dataracy.modules.reference.application.mapper.AuthorLevelDtoMapper;
import com.dataracy.modules.reference.application.port.out.AuthorLevelPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
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
class AuthorLevelQueryServiceTest {

    @Mock AuthorLevelPort authorlevelPort;
    @Mock AuthorLevelDtoMapper authorlevelDtoMapper;

    @InjectMocks AuthorLevelQueryService service;

    @Test
    @DisplayName("findAllAuthorLevels: 성공 - 전체 목록 반환")
    void findAllAuthorLevels_success() {
        // given
        List<AuthorLevel> domainList = List.of(new AuthorLevel(1L, "v1", "l1"), new AuthorLevel(2L, "v2", "l2"));
        AllAuthorLevelsResponse mapped = new AllAuthorLevelsResponse(List.of(new AuthorLevelResponse(1L, "v1", "l1"), new AuthorLevelResponse(2L, "v2", "l2")));
        given(authorlevelPort.findAllAuthorLevels()).willReturn(domainList);
        given(authorlevelDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllAuthorLevelsResponse result = service.findAllAuthorLevels();

        // then
        assertThat(result).isSameAs(mapped);
        then(authorlevelPort).should().findAllAuthorLevels();
        then(authorlevelDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("findAuthorLevel: 성공 - 단건 반환")
    void findAuthorLevel_success() {
        // given
        Long id = 10L;
        AuthorLevel domain = new AuthorLevel(id, "v", "l");
        AuthorLevelResponse mapped = new AuthorLevelResponse(id, "v", "l");
        given(authorlevelPort.findAuthorLevelById(id)).willReturn(Optional.of(domain));
        given(authorlevelDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        AuthorLevelResponse result = service.findAuthorLevel(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(authorlevelPort).should().findAuthorLevelById(id);
        then(authorlevelDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("findAuthorLevel: 실패 - 존재하지 않으면 ReferenceException")
    void findAuthorLevel_notFound_throws() {
        // given
        Long id = 999L;
        given(authorlevelPort.findAuthorLevelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.findAuthorLevel(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(authorlevelPort).should().findAuthorLevelById(id);
        then(authorlevelDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getLabelById: 성공 - 라벨 반환")
    void getLabelById_success() {
        // given
        Long id = 1L;
        given(authorlevelPort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(authorlevelPort).should().getLabelById(id);
    }

    @Test
    @DisplayName("getLabelById: 실패 - 없으면 ReferenceException")
    void getLabelById_notFound_throws() {
        // given
        Long id = 404L;
        given(authorlevelPort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.getLabelById(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(authorlevelPort).should().getLabelById(id);
    }

    @Test
    @DisplayName("validateAuthorLevel: 성공 - 존재하면 예외 없음")
    void validateAuthorLevel_success() {
        // given
        Long id = 1L;
        given(authorlevelPort.existsAuthorLevelById(id)).willReturn(true);

        // when
        service.validateAuthorLevel(id);

        // then
        then(authorlevelPort).should().existsAuthorLevelById(id);
    }

    @Test
    @DisplayName("validateAuthorLevel: 실패 - 존재하지 않으면 ReferenceException")
    void validateAuthorLevel_notFound_throws() {
        // given
        Long id = 2L;
        given(authorlevelPort.existsAuthorLevelById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(() -> service.validateAuthorLevel(id), ReferenceException.class);

        // then
        assertThat(ex).isNotNull();
        then(authorlevelPort).should().existsAuthorLevelById(id);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - 비어있으면 빈 맵, 값 있으면 위임 및 반환")
    void getLabelsByIds_success_and_emptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(authorlevelPort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(authorlevelPort).should().getLabelsByIds(ids);
    }
}
