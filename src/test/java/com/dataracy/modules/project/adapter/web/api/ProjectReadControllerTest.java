package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.project.adapter.web.api.read.ProjectReadController;
import com.dataracy.modules.project.adapter.web.mapper.read.ProjectReadWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.support.ParentProjectWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.support.ProjectConnectedDataWebMapper;
import com.dataracy.modules.project.adapter.web.response.read.ConnectedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ContinuedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ProjectDetailWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ParentProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import com.dataracy.modules.project.application.port.in.query.read.FindConnectedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindContinuedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetPopularProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetProjectDetailUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectReadController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectReadControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectReadWebMapper mapper;

    @MockBean
    private ProjectConnectedDataWebMapper projectConnectedDataWebMapper;

    @MockBean
    private ParentProjectWebMapper parentProjectWebMapper;

    @MockBean
    private GetProjectDetailUseCase getProjectDetailUseCase;

    @MockBean
    private FindContinuedProjectsUseCase findContinuedProjectsUseCase;

    @MockBean
    private FindConnectedProjectsUseCase findConnectedProjectsUseCase;

    @MockBean
    private GetPopularProjectsUseCase getPopularProjectsUseCase;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @MockBean
    private ExtractHeaderUtil extractHeaderUtil;

    @BeforeEach
    void setupResolver() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ProjectReadController(
                        extractHeaderUtil,
                        mapper,
                        getProjectDetailUseCase,
                        findContinuedProjectsUseCase,
                        findConnectedProjectsUseCase,
                        getPopularProjectsUseCase
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
    @DisplayName("프로젝트 상세 조회 성공 시 200 반환")
    void getProjectDetail() throws Exception {
        // given
        Long projectId = 10L;
        ProjectDetailResponse dto = new ProjectDetailResponse(
                projectId, "프로젝트 제목", 1L, "작성자", "자기소개", "profile.png",
                "레벨", "직업", "주제", "목적", "출처",
                true, 5L, "내용", "thumb.png", LocalDateTime.now(),
                12L, 34L, 56L, true, false,
                List.of(new ProjectConnectedDataResponse(
                        1L, "데이터셋1", 1L, "userA", "주제", "타입",
                        LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31),
                        "thumb.png", 100, 1000, 20, LocalDateTime.now(), 3L
                )),
                new ParentProjectResponse(
                        5L, "부모 프로젝트", "부모 내용", 1L, "작성자A",
                        1L, 2L, 3L, LocalDateTime.now()
                )
        );

        List<ProjectConnectedDataWebResponse> connectedWebResponses =
                dto.connectedDataSets().stream().map(projectConnectedDataWebMapper::toWebDto).toList();
        ParentProjectWebResponse parentWebResponse = parentProjectWebMapper.toWebDto(dto.parentProject());

        ProjectDetailWebResponse webRes = new ProjectDetailWebResponse(
                dto.id(), dto.title(), dto.creatorId(), dto.creatorName(), dto.userIntroductionText(),
                dto.userProfileImageUrl(), dto.authorLevelLabel(), dto.occupationLabel(),
                dto.topicLabel(), dto.analysisPurposeLabel(), dto.dataSourceLabel(),
                dto.isContinue(), dto.parentProjectId(), dto.content(), dto.projectThumbnailUrl(),
                dto.createdAt(), dto.commentCount(), dto.likeCount(), dto.viewCount(),
                dto.isLiked(), dto.hasChild(),
                connectedWebResponses,
                parentWebResponse
        );

        given(getProjectDetailUseCase.getProjectDetail(eq(projectId), anyLong(), anyString())).willReturn(dto);
        given(mapper.toWebDto(dto)).willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.GET_PROJECT_DETAIL.getCode()))
                .andExpect(jsonPath("$.data.title").value("프로젝트 제목"))
                .andExpect(jsonPath("$.data.creatorName").value("작성자"));
    }

    @Test
    @DisplayName("이어가기 프로젝트 조회 API 호출 시 200 응답 및 데이터 검증")
    void findContinueProjectsSuccess() throws Exception {
        // given
        Long projectId = 5L;
        ContinuedProjectResponse dto = new ContinuedProjectResponse(
                1L, "이어가기 제목", 1L, "작성자", "profile.png", "thumb.png",
                "주제", "레벨", 3L, 4L, 5L, LocalDateTime.now()
        );
        ContinuedProjectWebResponse webDto = new ContinuedProjectWebResponse(
                dto.id(), dto.title(), dto.creatorId(), dto.creatorName(), dto.userProfileUrl(),
                dto.projectThumbnailUrl(), dto.topicLabel(), dto.authorLevelLabel(),
                dto.commentCount(), dto.likeCount(), dto.viewCount(), dto.createdAt()
        );

        given(findContinuedProjectsUseCase.findContinuedProjects(eq(projectId), any()))
                .willReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 3), 1));
        given(mapper.toWebDto(any(ContinuedProjectResponse.class)))
                .willReturn(webDto);

        // when & then
        mockMvc.perform(get("/api/v1/projects/{projectId}/continue", projectId)
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.GET_CONTINUE_PROJECTS.getCode()))
                .andExpect(jsonPath("$.data.content[0].title").value("이어가기 제목"))
                .andExpect(jsonPath("$.data.content[0].creatorName").value("작성자"));
    }

    @Test
    @DisplayName("데이터와 연결된 프로젝트 조회 API 호출 시 200 응답 및 데이터 검증")
    void findConnectedProjectsSuccess() throws Exception {
        // given
        Long dataId = 10L;
        ConnectedProjectResponse dto = new ConnectedProjectResponse(
                2L, "연결 프로젝트 제목", 1L, "다른 작성자", "주제2",
                10L, 20L, 30L, LocalDateTime.now()
        );
        ConnectedProjectWebResponse webDto = new ConnectedProjectWebResponse(
                dto.id(), dto.title(), dto.creatorId(), dto.creatorName(), dto.topicLabel(),
                dto.commentCount(), dto.likeCount(), dto.viewCount(), dto.createdAt()
        );

        given(findConnectedProjectsUseCase.findConnectedProjects(eq(dataId), any()))
                .willReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 3), 1));
        given(mapper.toWebDto(any(ConnectedProjectResponse.class)))
                .willReturn(webDto);

        // when & then
        mockMvc.perform(get("/api/v1/projects/connected-to-dataset")
                        .param("dataId", String.valueOf(dataId))
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.GET_CONNECTED_PROJECTS_ASSOCIATED_DATA.getCode()))
                .andExpect(jsonPath("$.data.content[0].title").value("연결 프로젝트 제목"))
                .andExpect(jsonPath("$.data.content[0].creatorName").value("다른 작성자"))
                .andExpect(jsonPath("$.data.content[0].topicLabel").value("주제2"))
                .andExpect(jsonPath("$.data.content[0].commentCount").value(10))
                .andExpect(jsonPath("$.data.content[0].likeCount").value(20))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(30));
    }

    @Test
    @DisplayName("인기 프로젝트 조회 성공 시 200 반환")
    void getPopularProjects() throws Exception {
        // given
        int size = 2;
        PopularProjectResponse dto = new PopularProjectResponse(
                55L, "인기 프로젝트", "내용", 1L, "작성자", "popular.png",
                "주제", "목적", "출처", "레벨", 9L, 8L, 7L
        );
        PopularProjectWebResponse webRes = new PopularProjectWebResponse(
                dto.id(), dto.title(), dto.content(), dto.creatorId(), dto.creatorName(),
                dto.projectThumbnailUrl(), dto.topicLabel(), dto.analysisPurposeLabel(),
                dto.dataSourceLabel(), dto.authorLevelLabel(),
                dto.commentCount(), dto.likeCount(), dto.viewCount()
        );

        given(getPopularProjectsUseCase.getPopularProjects(size)).willReturn(List.of(dto));
        given(mapper.toWebDto(dto)).willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/projects/popular").param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_POPULAR_PROJECTS.getCode()))
                .andExpect(jsonPath("$.data[0].title").value("인기 프로젝트"))
                .andExpect(jsonPath("$.data[0].content").value("내용"));
    }
}
