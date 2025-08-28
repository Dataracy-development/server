package com.dataracy.modules.dataset.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.dataset.adapter.web.api.search.DataSearchController;
import com.dataracy.modules.dataset.adapter.web.mapper.search.DataFilterWebMapper;
import com.dataracy.modules.dataset.adapter.web.mapper.search.DataSearchWebMapper;
import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.read.RecentMinimalDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchFilteredDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchRealTimeDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchSimilarDataSetsUseCase;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = DataSearchController.class)
class DataSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DataSearchWebMapper dataSearchWebMapper;

    @MockBean
    private DataFilterWebMapper dataFilterWebMapper;

    @MockBean
    private SearchSimilarDataSetsUseCase searchSimilarDataSetsUseCase;

    @MockBean
    private SearchFilteredDataSetsUseCase searchFilteredDataSetsUseCase;

    @MockBean
    private SearchRealTimeDataSetsUseCase searchRealTimeDataSetsUseCase;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @Test
    @DisplayName("유사 데이터셋 조회 성공 시 200 반환")
    void searchSimilarDataSetsShouldReturnOk() throws Exception {
        // given
        Long dataId = 1L;
        int size = 3;

        SimilarDataResponse dto = new SimilarDataResponse(
                1L, "title", 1L, "userA", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 10, 1048576L,
                5, 3, LocalDateTime.of(2025, 8, 4, 10, 30)
        );
        SimilarDataWebResponse webRes = new SimilarDataWebResponse(
                1L, "title", 1L, "userA", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 10, 1048576L,
                5, 3, LocalDateTime.of(2025, 8, 4, 10, 30)
        );

        given(searchSimilarDataSetsUseCase.searchSimilarDataSets(eq(dataId), eq(size)))
                .willReturn(List.of(dto));
        given(dataSearchWebMapper.toWebDto(dto))
                .willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/datasets/{dataId}/similar", 1L)
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.FIND_SIMILAR_DATASETS.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.FIND_SIMILAR_DATASETS.getMessage()))
                .andExpect(jsonPath("$.data[0].title").value("title"))
                .andExpect(jsonPath("$.data[0].topicLabel").value("topic"));
    }

    @Test
    @DisplayName("필터링 검색 성공 시 200 반환 및 페이징 결과 확인")
    void searchFilteredDataSetsShouldReturnOk() throws Exception {
        // given
        FilteringDataWebRequest webReq =
                new FilteringDataWebRequest(null, null, null, null, null, null);
        FilteringDataRequest reqDto =
                new FilteringDataRequest(null, null, null, null, null, null);

        FilteredDataResponse dto = new FilteredDataResponse(
                1L, "filtered-title", 1L, "userA", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 5, 2048L, 20, 10,
                LocalDateTime.of(2025, 8, 4, 11, 0), 2L
        );
        FilteredDataWebResponse webRes = new FilteredDataWebResponse(
                1L, "filtered-title", 1L, "userA", "topic", "source", "type",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5),
                "desc", "thumb.png", 5, 2048L, 20, 10,
                LocalDateTime.of(2025, 8, 4, 11, 0), 2L
        );

        given(dataFilterWebMapper.toApplicationDto(any()))
                .willReturn(reqDto);
        given(searchFilteredDataSetsUseCase.searchFilteredDataSets(eq(reqDto), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(dto)));
        given(dataFilterWebMapper.toWebDto(dto))
                .willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/datasets/filter")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.FIND_FILTERED_DATASETS.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.FIND_FILTERED_DATASETS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].title").value("filtered-title"))
                .andExpect(jsonPath("$.data.content[0].countConnectedProjects").value(2));
    }

    @Test
    @DisplayName("실시간 데이터셋 검색 성공 시 200 반환")
    void getRealTimeDataSetsShouldReturnOk() throws Exception {
        // given
        String keyword = "경제";
        int size = 2;

        RecentMinimalDataResponse dto = new RecentMinimalDataResponse(
                99L, "실시간데이터", 1L, "userA", "thumb.png", LocalDateTime.of(2025, 8, 25, 10, 0)
        );
        RecentMinimalDataWebResponse webRes = new RecentMinimalDataWebResponse(
                99L, "실시간데이터", 1L, "userA", "thumb.png", LocalDateTime.of(2025, 8, 25, 10, 0)
        );

        given(searchRealTimeDataSetsUseCase.searchRealTimeDataSets(keyword, size))
                .willReturn(List.of(dto));
        given(dataSearchWebMapper.toWebDto(dto))
                .willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/datasets/search/real-time")
                        .param("keyword", keyword)
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.FIND_REAL_TIME_DATASETS.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.FIND_REAL_TIME_DATASETS.getMessage()))
                .andExpect(jsonPath("$.data[0].title").value("실시간데이터"))
                .andExpect(jsonPath("$.data[0].dataThumbnailUrl").value("thumb.png"));
    }
}
