package com.dataracy.modules.dataset.adapter.web;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.dataset.adapter.web.api.search.DataSearchController;
import com.dataracy.modules.dataset.adapter.web.mapper.search.DataFilterWebMapper;
import com.dataracy.modules.dataset.adapter.web.mapper.search.DataSearchWebMapper;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchFilteredDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchSimilarDataSetsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DataSearchControllerTest {

    @Mock
    private DataSearchWebMapper dataSearchWebMapper;
    @Mock
    private DataFilterWebMapper dataFilterWebMapper;
    @Mock
    private SearchSimilarDataSetsUseCase searchSimilarDataSetsUseCase;
    @Mock
    private SearchFilteredDataSetsUseCase searchFilteredDataSetsUseCase;

    @InjectMocks
    private DataSearchController controller;

    @Test
    @DisplayName("유사 데이터셋 조회 시 성공 응답을 반환한다")
    void searchSimilarDataSetsShouldReturnResponses() {
        // given
        Long dataId = 1L;
        int size = 3;

        SimilarDataResponse dto = new SimilarDataResponse(
                1L, "title", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 10, 1048576L,
                5, 3, LocalDateTime.of(2025, 8, 4, 10, 30)
        );

        given(searchSimilarDataSetsUseCase.searchSimilarDataSets(eq(dataId), eq(size)))
                .willReturn(List.of(dto));

        SimilarDataWebResponse webResponse = new SimilarDataWebResponse(
                1L, "title", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 10, 1048576L,
                5, 3, LocalDateTime.of(2025, 8, 4, 10, 30)
        );
        given(dataSearchWebMapper.toWebDto(dto)).willReturn(webResponse);

        // when
        ResponseEntity<?> response = controller.searchSimilarDataSets(dataId, size);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        List<SimilarDataWebResponse> body = ((SuccessResponse<List<SimilarDataWebResponse>>) response.getBody()).getData();
        assertThat(body).hasSize(1);
        assertThat(body.get(0).title()).isEqualTo("title");
    }

    @Test
    @DisplayName("필터링 검색 시 페이징 결과 반환")
    void searchFilteredDataSetsShouldReturnPage() {
        // given
        FilteringDataRequest reqDto = new FilteringDataRequest(null, null, null, null, null, null);
        given(dataFilterWebMapper.toApplicationDto(any())).willReturn(reqDto);

        FilteredDataResponse dto = new FilteredDataResponse(
                1L, "filtered-title", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 5, 2048L, 20, 10,
                LocalDateTime.of(2025, 8, 4, 11, 0), 2L
        );
        given(searchFilteredDataSetsUseCase.searchFilteredDataSets(eq(reqDto), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(dto)));

        FilteredDataWebResponse webResponse = new FilteredDataWebResponse(
                1L, "filtered-title", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 5, 2048L, 20, 10,
                LocalDateTime.of(2025, 8, 4, 11, 0), 2L
        );
        given(dataFilterWebMapper.toWebDto(dto)).willReturn(webResponse);

        // when
        ResponseEntity<?> response = controller.searchFilteredDataSets(null, Pageable.ofSize(5));

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        com.dataracy.modules.common.dto.response.SuccessResponse<org.springframework.data.domain.Page<FilteredDataWebResponse>> body =
                (SuccessResponse<Page<FilteredDataWebResponse>>) response.getBody();

        assertThat(body.getData().getContent()).hasSize(1);
        assertThat(body.getData().getContent().get(0).title()).isEqualTo("filtered-title");
        assertThat(body.getData().getContent().get(0).countConnectedProjects()).isEqualTo(2L);
    }
}
