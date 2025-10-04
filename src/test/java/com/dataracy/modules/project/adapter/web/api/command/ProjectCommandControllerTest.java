package com.dataracy.modules.project.adapter.web.api.command;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
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

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers =
        com.dataracy.modules.project.adapter.web.api.command.ProjectCommandController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class ProjectCommandControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ProjectCommandWebMapper projectCommandWebMapper;

  @MockBean private UploadProjectUseCase uploadProjectUseCase;

  @MockBean private ModifyProjectUseCase modifyProjectUseCase;

  @MockBean private DeleteProjectUseCase deleteProjectUseCase;

  @MockBean private RestoreProjectUseCase restoreProjectUseCase;

  // 공통 모킹
  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
  @MockBean private JwtValidateUseCase jwtValidateUseCase;
  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;
  @MockBean private com.dataracy.modules.common.logging.ApiLogger apiLogger;
  @MockBean private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

  @BeforeEach
  void setupResolver() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(
                new com.dataracy.modules.project.adapter.web.api.command.ProjectCommandController(
                    projectCommandWebMapper,
                    uploadProjectUseCase,
                    modifyProjectUseCase,
                    deleteProjectUseCase,
                    restoreProjectUseCase))
            .setCustomArgumentResolvers(currentUserIdArgumentResolver)
            .build();

    // 모든 @CurrentUserId → userId=1L
    given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
    given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
  }

  @Test
  @DisplayName("uploadProject API: 성공 - 201 Created와 JSON 응답 검증")
  void uploadProjectSuccess() throws Exception {
    // given
    Long userId = 1L;
    MockMultipartFile thumbnailFile =
        new MockMultipartFile(
            "thumbnailFile",
            "thumbnail.jpg",
            "image/jpeg",
            "test image content".getBytes(StandardCharsets.UTF_8));

    UploadProjectWebRequest webRequest =
        new UploadProjectWebRequest(
            "Test Project",
            1L, // topicId
            2L, // analysisPurposeId
            3L, // dataSourceId
            4L, // authorLevelId
            true, // isContinue
            5L, // parentProjectId
            "Test Content",
            List.of(1L, 2L) // dataIds
            );

    UploadProjectRequest requestDto =
        new UploadProjectRequest(
            "Test Project",
            1L, // topicId
            2L, // analysisPurposeId
            3L, // dataSourceId
            4L, // authorLevelId
            true, // isContinue
            5L, // parentProjectId
            "Test Content",
            List.of(1L, 2L) // dataIds
            );

    UploadProjectResponse responseDto = new UploadProjectResponse(1L);
    UploadProjectWebResponse webResponse = new UploadProjectWebResponse(1L);

    given(projectCommandWebMapper.toApplicationDto(webRequest)).willReturn(requestDto);
    given(uploadProjectUseCase.uploadProject(userId, thumbnailFile, requestDto))
        .willReturn(responseDto);
    given(projectCommandWebMapper.toWebDto(responseDto)).willReturn(webResponse);

    // when & then
    MockMultipartFile webRequestFile =
        new MockMultipartFile(
            "webRequest",
            "webRequest.json",
            "application/json",
            objectMapper.writeValueAsBytes(webRequest));

    mockMvc
        .perform(
            multipart("/api/v1/projects")
                .file(thumbnailFile)
                .file(webRequestFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.CREATED_PROJECT.getCode()))
        .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.CREATED_PROJECT.getMessage()))
        .andExpect(jsonPath("$.data.id").value(1));

    then(projectCommandWebMapper).should().toApplicationDto(webRequest);
    then(uploadProjectUseCase).should().uploadProject(userId, thumbnailFile, requestDto);
    then(projectCommandWebMapper).should().toWebDto(responseDto);
  }

  @Test
  @DisplayName("modifyProject API: 성공 - 200 OK와 JSON 응답 검증")
  void modifyProjectSuccess() throws Exception {
    // given
    Long projectId = 1L;
    MockMultipartFile thumbnailFile =
        new MockMultipartFile(
            "thumbnailFile",
            "thumbnail.jpg",
            "image/jpeg",
            "test image content".getBytes(StandardCharsets.UTF_8));

    ModifyProjectWebRequest webRequest =
        new ModifyProjectWebRequest(
            "Modified Project",
            1L, // topicId
            2L, // analysisPurposeId
            3L, // dataSourceId
            4L, // authorLevelId
            true, // isContinue
            5L, // parentProjectId
            "Modified Content",
            List.of(1L, 2L) // dataIds
            );

    ModifyProjectRequest requestDto =
        new ModifyProjectRequest(
            "Modified Project",
            1L, // topicId
            2L, // analysisPurposeId
            3L, // dataSourceId
            4L, // authorLevelId
            true, // isContinue
            5L, // parentProjectId
            "Modified Content",
            List.of(1L, 2L) // dataIds
            );

    given(projectCommandWebMapper.toApplicationDto(webRequest)).willReturn(requestDto);
    willDoNothing().given(modifyProjectUseCase).modifyProject(projectId, thumbnailFile, requestDto);

    // when & then
    MockMultipartFile webRequestFile =
        new MockMultipartFile(
            "webRequest",
            "webRequest.json",
            "application/json",
            objectMapper.writeValueAsBytes(webRequest));

    mockMvc
        .perform(
            multipart("/api/v1/projects/{projectId}", projectId)
                .file(thumbnailFile)
                .file(webRequestFile)
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.MODIFY_PROJECT.getCode()))
        .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.MODIFY_PROJECT.getMessage()));

    then(projectCommandWebMapper).should().toApplicationDto(webRequest);
    then(modifyProjectUseCase).should().modifyProject(projectId, thumbnailFile, requestDto);
  }

  @Test
  @DisplayName("deleteProject API: 성공 - 200 OK와 JSON 응답 검증")
  void deleteProjectSuccess() throws Exception {
    // given
    Long projectId = 1L;

    willDoNothing().given(deleteProjectUseCase).deleteProject(projectId);

    // when & then
    mockMvc
        .perform(delete("/api/v1/projects/{projectId}", projectId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.DELETE_PROJECT.getCode()))
        .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.DELETE_PROJECT.getMessage()));

    then(deleteProjectUseCase).should().deleteProject(projectId);
  }

  @Test
  @DisplayName("restoreProject API: 성공 - 200 OK와 JSON 응답 검증")
  void restoreProjectSuccess() throws Exception {
    // given
    Long projectId = 1L;

    willDoNothing().given(restoreProjectUseCase).restoreProject(projectId);

    // when & then
    mockMvc
        .perform(patch("/api/v1/projects/{projectId}/restore", projectId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.RESTORE_PROJECT.getCode()))
        .andExpect(jsonPath("$.message").value(ProjectSuccessStatus.RESTORE_PROJECT.getMessage()));

    then(restoreProjectUseCase).should().restoreProject(projectId);
  }
}
