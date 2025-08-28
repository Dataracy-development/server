package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.project.adapter.web.api.search.ProjectSearchController;
import com.dataracy.modules.project.adapter.web.mapper.search.ProjectFilterWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.search.ProjectSearchWebMapper;
import com.dataracy.modules.project.adapter.web.request.search.FilteringProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.search.FilteredProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.SimilarProjectWebResponse;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import com.dataracy.modules.project.application.port.in.query.search.SearchFilteredProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchRealTimeProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchSimilarProjectsUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ProjectSearchController.class)
class ProjectSearchControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ===== MockBean =====
    @MockBean
    private ProjectSearchWebMapper projectSearchWebMapper;

    @MockBean
    private ProjectFilterWebMapper projectFilterWebMapper;

    @MockBean
    private SearchRealTimeProjectsUseCase searchRealTimeProjectsUseCase;

    @MockBean
    private SearchSimilarProjectsUseCase searchSimilarProjectsUseCase;

    @MockBean
    private SearchFilteredProjectsUseCase searchFilteredProjectsUsecase;

    // 공통 모킹 (보안/로그 관련)
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @MockBean
    private ExtractHeaderUtil extractHeaderUtil;

    @BeforeEach
    void setupMocks(){
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ProjectSearchController(
                        projectSearchWebMapper,
                        projectFilterWebMapper,
                        searchRealTimeProjectsUseCase,
                        searchSimilarProjectsUseCase,
                        searchFilteredProjectsUsecase
                ))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
        given(jwtValidateUseCase.getUserIdFromToken(any())).willReturn(1L);
        given(extractHeaderUtil.extractAuthenticatedUserIdFromRequest(any())).willReturn(1L);
        given(extractHeaderUtil.extractViewerIdFromRequest(any(), any())).willReturn("viewer-123");
    }

    @Test
    @DisplayName("실시간 프로젝트 검색 API 호출 시 200 응답 및 데이터 검증")
    void searchRealTimeProjectsSuccess() throws Exception {
        // given
        String keyword = "데이터";
        int size = 2;

        RealTimeProjectResponse dto = new RealTimeProjectResponse(
                1L, "실시간 프로젝트", 1L, "작성자", "https://~~","thumb.png"
        );
        RealTimeProjectWebResponse webDto = new RealTimeProjectWebResponse(
                dto.id(), dto.title(), dto.creatorId(), dto.creatorName(), dto.userProfileImageUrl(), dto.projectThumbnailUrl()
        );

        given(searchRealTimeProjectsUseCase.searchByKeyword(keyword, size)).willReturn(List.of(dto));
        given(projectSearchWebMapper.toWebDto(dto)).willReturn(webDto);

        // when & then
        mockMvc.perform(get("/api/v1/projects/search/real-time")
                        .param("keyword", keyword)
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS.getCode()))
                .andExpect(jsonPath("$.data[0].title").value("실시간 프로젝트"))
                .andExpect(jsonPath("$.data[0].projectThumbnailUrl").value("thumb.png"));
    }

    @Test
    @DisplayName("유사 프로젝트 검색 API 호출 시 200 응답 및 데이터 검증")
    void searchSimilarProjectsSuccess() throws Exception {
        // given
        Long projectId = 10L;
        int size = 2;

        SimilarProjectResponse dto = new SimilarProjectResponse(
                2L, "유사 프로젝트", "내용", 1L, "작성자", "https://~~",
                "thumb.png", "주제", "목적", "출처", "레벨",
                5L, 10L, 15L
        );
        SimilarProjectWebResponse webDto = new SimilarProjectWebResponse(
                dto.id(), dto.title(), dto.content(), dto.creatorId(), dto.creatorName(), dto.userProfileImageUrl(),
                dto.projectThumbnailUrl(), dto.topicLabel(), dto.analysisPurposeLabel(),
                dto.dataSourceLabel(), dto.authorLevelLabel(),
                dto.commentCount(), dto.likeCount(), dto.viewCount()
        );

        given(searchSimilarProjectsUseCase.searchSimilarProjects(eq(projectId), eq(size))).willReturn(List.of(dto));
        given(projectSearchWebMapper.toWebDto(dto)).willReturn(webDto);

        // when & then
        mockMvc.perform(get("/api/v1/projects/{projectId}/similar", projectId)
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS.getCode()))
                .andExpect(jsonPath("$.data[0].title").value("유사 프로젝트"))
                .andExpect(jsonPath("$.data[0].content").value("내용"))
                .andExpect(jsonPath("$.data[0].creatorName").value("작성자"));
    }

    @Test
    @DisplayName("필터링된 프로젝트 검색 API 호출 시 200 응답 및 데이터 검증")
    void searchFilteredProjectsSuccess() throws Exception {
        // given
        FilteringProjectWebRequest webRequest = new FilteringProjectWebRequest(
                "키워드", "최신순", 1L, 2L, 3L, 4L
        );
        FilteringProjectRequest requestDto = new FilteringProjectRequest(
                "키워드", "최신순", 1L, 2L, 3L, 4L
        );

        FilteredProjectResponse dto = new FilteredProjectResponse(
                3L, "필터링된 프로젝트", "내용", 1L, "작성자", "https://~~",
                "thumb.png", "주제", "목적", "출처", "레벨",
                3L, 4L, 5L, LocalDateTime.now(), new ArrayList<>()
        );
        FilteredProjectWebResponse webDto = new FilteredProjectWebResponse(
                dto.id(), dto.title(), dto.content(), dto.creatorId(), dto.creatorName(), dto.userProfileImageUrl(),
                dto.projectThumbnailUrl(), dto.topicLabel(), dto.analysisPurposeLabel(),
                dto.dataSourceLabel(), dto.authorLevelLabel(),
                dto.commentCount(), dto.likeCount(), dto.viewCount(), dto.createdAt(), new ArrayList<>()
        );

        Pageable pageable = PageRequest.of(0, 5);
        Page<FilteredProjectResponse> dtoPage = new PageImpl<>(
                new ArrayList<>(List.of(dto)),
                pageable,
                1
        );

        given(projectFilterWebMapper.toApplicationDto(any(FilteringProjectWebRequest.class))).willReturn(requestDto);
        given(searchFilteredProjectsUsecase.searchByFilters(eq(requestDto), any(Pageable.class))).willReturn(dtoPage);
        given(projectFilterWebMapper.toWebDto(dto)).willReturn(webDto);

        // when & then
        mockMvc.perform(get("/api/v1/projects/filter")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_FILTERED_PROJECTS.getCode()))
                .andExpect(jsonPath("$.data.content[0].title").value("필터링된 프로젝트"))
                .andExpect(jsonPath("$.data.content[0].creatorName").value("작성자"));
    }
}
