package com.dataracy.modules.project.adapter.web.api.read;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.project.adapter.web.mapper.read.ProjectReadWebMapper;
import com.dataracy.modules.project.adapter.web.response.read.ProjectDetailWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.UserProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.application.port.in.query.read.GetProjectDetailUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetPopularProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindUserProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindContinuedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindConnectedProjectsUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProjectReadControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectReadWebMapper projectReadWebMapper;

    @MockBean
    private GetProjectDetailUseCase getProjectDetailUseCase;

    @MockBean
    private GetPopularProjectsUseCase getPopularProjectsUseCase;

    @MockBean
    private FindUserProjectsUseCase findUserProjectsUseCase;

    @MockBean
    private FindContinuedProjectsUseCase findContinuedProjectsUseCase;

    @MockBean
    private FindConnectedProjectsUseCase findConnectedProjectsUseCase;

    @MockBean
    private ExtractHeaderUtil extractHeaderUtil;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;
    @MockBean
    private com.dataracy.modules.common.logging.ApiLogger apiLogger;
    @MockBean
    private com.dataracy.modules.common.config.web.MultipartJackson2HttpMessageConverter multipartJackson2HttpMessageConverter;
    
    @MockBean
    private com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver currentUserIdArgumentResolver;
    
    

    @Test
    @DisplayName("getProjectDetail API: 성공 - 200 OK와 JSON 응답 검증")
    void getProjectDetailSuccess() throws Exception {
        // given
        Long authenticatedUserId = 1L;
        Long viewerId = 2L;
        Long projectId = 1L;

        ProjectDetailResponse responseDto = new ProjectDetailResponse(
                1L, "Test Project", 1L, "Test User", "profile.jpg", "Introduction",
                "Expert", "Developer", "AI", "Research", "Public", true, 1L,
                "Content", "thumbnail.jpg", LocalDateTime.now(), 10L, 5L, 100L,
                false, true, List.of(), null
        );

        ProjectDetailWebResponse webResponse = new ProjectDetailWebResponse(
                1L, "Test Project", 1L, "Test User", "profile.jpg", "Introduction",
                "Expert", "Developer", "AI", "Research", "Public", true, 1L,
                "Content", "thumbnail.jpg", LocalDateTime.now(), 10L, 5L, 100L,
                false, true, List.of(), null
        );

        given(extractHeaderUtil.extractAuthenticatedUserIdFromRequest(any())).willReturn(authenticatedUserId);
        given(extractHeaderUtil.extractViewerIdFromRequest(any(), any())).willReturn(String.valueOf(viewerId));
        given(getProjectDetailUseCase.getProjectDetail(projectId, authenticatedUserId, String.valueOf(viewerId))).willReturn(responseDto);
        given(projectReadWebMapper.toWebDto(responseDto)).willReturn(webResponse);

        // when & then
        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.GET_PROJECT_DETAIL.getCode()))
                .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.GET_PROJECT_DETAIL.getMessage()))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Project"));

        then(extractHeaderUtil).should().extractAuthenticatedUserIdFromRequest(any());
        then(extractHeaderUtil).should().extractViewerIdFromRequest(any(), any());
        then(getProjectDetailUseCase).should().getProjectDetail(projectId, authenticatedUserId, String.valueOf(viewerId));
        then(projectReadWebMapper).should().toWebDto(responseDto);
    }

    @Test
    @DisplayName("getPopularProjects API: 성공 - 200 OK와 JSON 응답 검증")
    void getPopularProjectsSuccess() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        PopularProjectResponse project1 = new PopularProjectResponse(
                1L, "Popular Project 1", "Content 1", 1L, "User 1", "profile1.jpg",
                "thumb1.jpg", "AI", "Research", "Public", "Expert", 10L, 5L, 100L
        );

        PopularProjectWebResponse webProject1 = new PopularProjectWebResponse(
                1L, "Popular Project 1", "Content 1", 1L, "User 1", "profile1.jpg",
                "thumb1.jpg", "AI", "Research", "Public", "Expert", 10L, 5L, 100L
        );

        given(getPopularProjectsUseCase.getPopularProjects(10)).willReturn(List.of(project1));
        given(projectReadWebMapper.toWebDto(project1)).willReturn(webProject1);

        // when & then
        mockMvc.perform(get("/api/v1/projects/popular")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_POPULAR_PROJECTS.getCode()))
                .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.FIND_POPULAR_PROJECTS.getMessage()))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Popular Project 1"));

        then(getPopularProjectsUseCase).should().getPopularProjects(10);
        then(projectReadWebMapper).should().toWebDto(project1);
    }

    @Test
    @DisplayName("getUserProjects API: 성공 - 200 OK와 JSON 응답 검증")
    void getUserProjectsSuccess() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 5); // 요청과 일치하도록 수정
        UserProjectResponse project1 = new UserProjectResponse(
                1L, "User Project 1", "Content 1", "thumb1.jpg", "AI", "Expert",
                10L, 5L, 100L, LocalDateTime.now()
        );
        Page<UserProjectResponse> responsePage = new PageImpl<>(List.of(project1), pageable, 1);

        UserProjectWebResponse webProject1 = new UserProjectWebResponse(
                1L, "User Project 1", "Content 1", "thumb1.jpg", "AI", "Expert",
                10L, 5L, 100L, LocalDateTime.now()
        );

        given(findUserProjectsUseCase.findUserProjects(any(), any(Pageable.class))).willReturn(responsePage);
        // Page는 map() 메서드로 처리되므로 개별 항목을 매핑
        given(projectReadWebMapper.toWebDto(project1)).willReturn(webProject1);

        // when & then
        mockMvc.perform(get("/api/v1/projects/me")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.GET_USER_PROJECTS.getCode()))
                .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.GET_USER_PROJECTS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("User Project 1"));

        then(findUserProjectsUseCase).should().findUserProjects(any(), any(Pageable.class));
        then(projectReadWebMapper).should().toWebDto(project1);
    }
}