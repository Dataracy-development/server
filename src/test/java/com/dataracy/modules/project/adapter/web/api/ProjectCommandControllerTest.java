package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.project.adapter.web.api.command.ProjectCommandController;
import com.dataracy.modules.project.adapter.web.mapper.command.ProjectCommandWebMapper;
import com.dataracy.modules.project.adapter.web.request.command.ModifyProjectWebRequest;
import com.dataracy.modules.project.adapter.web.request.command.UploadProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.command.UploadProjectWebResponse;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.dto.response.command.UploadProjectResponse;
import com.dataracy.modules.project.application.port.in.command.content.DeleteProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.ModifyProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.RestoreProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.UploadProjectUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ProjectCommandController.class)
class ProjectCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectCommandWebMapper mapper;

    @MockBean
    private UploadProjectUseCase uploadProjectUseCase;

    @MockBean
    private ModifyProjectUseCase modifyProjectUseCase;

    @MockBean
    private DeleteProjectUseCase deleteProjectUseCase;

    @MockBean
    private RestoreProjectUseCase restoreProjectUseCase;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @BeforeEach
    void setupResolver() throws Exception {
        given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
        given(jwtValidateUseCase.getUserIdFromToken(any())).willReturn(1L);
    }

    @Test
    @DisplayName("프로젝트 업로드 성공 시 201 반환")
    void uploadProject() throws Exception {
        // given
        MockMultipartFile thumb = new MockMultipartFile("thumbnailFile", "a.png", "image/png", "img".getBytes());

        UploadProjectWebRequest webReq = new UploadProjectWebRequest(
                "프로젝트 제목",
                2L,
                3L,
                4L,
                5L,
                true,
                10L,
                "내용입니다.",
                List.of(1L, 2L, 3L)
        );
        UploadProjectRequest dto = new UploadProjectRequest("프로젝트 제목", 2L, 3L, 4L, 5L, true, 10L, "내용입니다.", List.of(1L, 2L, 3L));
        UploadProjectResponse resDto = new UploadProjectResponse(1L);
        UploadProjectWebResponse webRes = new UploadProjectWebResponse(1L);

        given(mapper.toApplicationDto(any(UploadProjectWebRequest.class))).willReturn(dto);
        given(uploadProjectUseCase.uploadProject(eq(1L), any(), eq(dto))).willReturn(resDto);
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "webRequest", "", "application/json",
                objectMapper.writeValueAsBytes(webReq));

        // when & then
        mockMvc.perform(multipart("/api/v1/projects")
                        .file(thumb)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.CREATED_PROJECT.getCode()))
                .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.CREATED_PROJECT.getMessage()))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("프로젝트 수정 성공 시 200 반환")
    void modifyProject() throws Exception {
        // given
        Long projectId = 1L;
        MockMultipartFile thumb = new MockMultipartFile("thumbnailFile", "b.png", "image/png", "img".getBytes());

        ModifyProjectWebRequest webReq = new ModifyProjectWebRequest(
                "수정된 제목",
                2L,
                3L,
                4L,
                5L,
                false,
                null,
                "수정된 내용",
                List.of(5L, 6L)
        );
        ModifyProjectRequest dto = new ModifyProjectRequest("수정된 제목", 2L, 3L, 4L, 5L, false, null, "수정된 내용", List.of(5L, 6L));

        given(mapper.toApplicationDto(any(ModifyProjectWebRequest.class))).willReturn(dto);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "webRequest", "", "application/json",
                objectMapper.writeValueAsBytes(webReq));

        // when & then
        mockMvc.perform(multipart("/api/v1/projects/{projectId}", projectId)
                        .file(thumb)
                        .file(jsonPart)
                        .with(req -> { req.setMethod("PUT"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.MODIFY_PROJECT.getCode()))
                .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.MODIFY_PROJECT.getMessage()));

        then(modifyProjectUseCase).should().modifyProject(eq(projectId), any(), eq(dto));
    }

    @Test
    @DisplayName("프로젝트 삭제 성공 시 200 반환")
    void deleteProject() throws Exception {
        // given
        Long projectId = 2L;

        // when & then
        mockMvc.perform(delete("/api/v1/projects/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.DELETE_PROJECT.getCode()))
                .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.DELETE_PROJECT.getMessage()));

        then(deleteProjectUseCase).should().deleteProject(projectId);
    }

    @Test
    @DisplayName("프로젝트 복원 성공 시 200 반환")
    void restoreProject() throws Exception {
        // given
        Long projectId = 3L;

        // when & then
        mockMvc.perform(patch("/api/v1/projects/{projectId}/restore", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.RESTORE_PROJECT.getCode()))
                .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.RESTORE_PROJECT.getMessage()));

        then(restoreProjectUseCase).should().restoreProject(projectId);
    }
}
