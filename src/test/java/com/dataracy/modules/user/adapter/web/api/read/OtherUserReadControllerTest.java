package com.dataracy.modules.user.adapter.web.api.read;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.user.adapter.web.mapper.read.OtherUserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetOtherUserInfoUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OtherUserReadControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private OtherUserReadWebMapper otherUserReadWebMapper;

  @MockBean private GetOtherUserInfoUseCase getOtherUserInfoUseCase;

  // 공통 모킹
  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
  @MockBean private JwtValidateUseCase jwtValidateUseCase;
  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

  @Test
  @DisplayName("getOtherUserInfo API: 성공 - 200 OK와 JSON 응답 검증")
  void getOtherUserInfoSuccess() throws Exception {
    // given
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    GetOtherUserProjectResponse project1 =
        new GetOtherUserProjectResponse(
            1L,
            "Project 1",
            "Content 1",
            "AI",
            "Expert",
            "test1.jpg",
            10L,
            5L,
            100L,
            LocalDateTime.now());
    GetOtherUserDataResponse data1 =
        new GetOtherUserDataResponse(
            1L,
            "Data 1",
            "AI",
            "CSV",
            LocalDate.now(),
            LocalDate.now(),
            "thumb1.jpg",
            10,
            1024L,
            100,
            5,
            LocalDateTime.now(),
            2L);

    Page<GetOtherUserProjectResponse> projectsPage = new PageImpl<>(List.of(project1), pageable, 1);
    Page<GetOtherUserDataResponse> datasetsPage = new PageImpl<>(List.of(data1), pageable, 1);

    GetOtherUserInfoResponse responseDto =
        new GetOtherUserInfoResponse(
            1L,
            "Test User",
            "Expert",
            "Developer",
            "profile.jpg",
            "Introduction",
            projectsPage,
            datasetsPage);

    GetOtherUserInfoWebResponse webResponse =
        new GetOtherUserInfoWebResponse(
            1L,
            "Test User",
            "Expert",
            "Developer",
            "profile.jpg",
            "Introduction",
            new PageImpl<>(
                List.of(
                    new GetOtherUserProjectWebResponse(
                        1L,
                        "Project 1",
                        "Content 1",
                        "AI",
                        "Expert",
                        "test1.jpg",
                        10L,
                        5L,
                        100L,
                        LocalDateTime.now())),
                pageable,
                1),
            new PageImpl<>(
                List.of(
                    new GetOtherUserDataWebResponse(
                        1L,
                        "Data 1",
                        "AI",
                        "CSV",
                        LocalDate.now(),
                        LocalDate.now(),
                        "thumb1.jpg",
                        10,
                        1024L,
                        100,
                        5,
                        LocalDateTime.now(),
                        2L)),
                pageable,
                1));

    given(getOtherUserInfoUseCase.getOtherUserInfo(userId)).willReturn(responseDto);
    given(otherUserReadWebMapper.toWebDto(responseDto)).willReturn(webResponse);

    // when & then
    mockMvc
        .perform(get("/api/v1/users/{userId}", userId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_GET_OTHER_USER_INFO.getCode()))
        .andExpect(
            jsonPath("$.message").value(UserSuccessStatus.OK_GET_OTHER_USER_INFO.getMessage()))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.nickname").value("Test User"));

    then(getOtherUserInfoUseCase).should().getOtherUserInfo(userId);
    then(otherUserReadWebMapper).should().toWebDto(responseDto);
  }

  @Test
  @DisplayName("getOtherUserData API: 성공 - 200 OK와 JSON 응답 검증")
  void getOtherUserDataSuccess() throws Exception {
    // given
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    GetOtherUserDataResponse data1 =
        new GetOtherUserDataResponse(
            1L,
            "Data 1",
            "AI",
            "CSV",
            LocalDate.now(),
            LocalDate.now(),
            "thumb1.jpg",
            10,
            1024L,
            100,
            5,
            LocalDateTime.now(),
            2L);
    Page<GetOtherUserDataResponse> responsePage = new PageImpl<>(List.of(data1), pageable, 1);

    GetOtherUserDataWebResponse webData1 =
        new GetOtherUserDataWebResponse(
            1L,
            "Data 1",
            "AI",
            "CSV",
            LocalDate.now(),
            LocalDate.now(),
            "thumb1.jpg",
            10,
            1024L,
            100,
            5,
            LocalDateTime.now(),
            2L);

    given(getOtherUserInfoUseCase.getOtherExtraDataSets(userId, pageable)).willReturn(responsePage);
    given(otherUserReadWebMapper.toWebDto(data1)).willReturn(webData1);

    // when & then
    mockMvc
        .perform(
            get("/api/v1/users/{userId}/datasets", userId)
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_DATASETS.getCode()))
        .andExpect(
            jsonPath("$.message").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_DATASETS.getMessage()))
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[0].title").value("Data 1"));

    then(getOtherUserInfoUseCase).should().getOtherExtraDataSets(userId, pageable);
    then(otherUserReadWebMapper).should().toWebDto(data1);
  }

  @Test
  @DisplayName("getOtherUserProjects API: 성공 - 200 OK와 JSON 응답 검증")
  void getOtherUserProjectsSuccess() throws Exception {
    // given
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    GetOtherUserProjectResponse project1 =
        new GetOtherUserProjectResponse(
            1L,
            "Project 1",
            "Content 1",
            "AI",
            "Expert",
            "test1.jpg",
            10L,
            5L,
            100L,
            LocalDateTime.now());
    GetOtherUserProjectResponse project2 =
        new GetOtherUserProjectResponse(
            2L,
            "Project 2",
            "Content 2",
            "DATA",
            "Expert",
            "test2.jpg",
            20L,
            10L,
            200L,
            LocalDateTime.now());
    Page<GetOtherUserProjectResponse> responsePage =
        new PageImpl<>(List.of(project1, project2), pageable, 2);

    GetOtherUserProjectWebResponse webProject1 =
        new GetOtherUserProjectWebResponse(
            1L,
            "Project 1",
            "Content 1",
            "AI",
            "Expert",
            "test1.jpg",
            10L,
            5L,
            100L,
            LocalDateTime.now());
    GetOtherUserProjectWebResponse webProject2 =
        new GetOtherUserProjectWebResponse(
            2L,
            "Project 2",
            "Content 2",
            "DATA",
            "Expert",
            "test2.jpg",
            20L,
            10L,
            200L,
            LocalDateTime.now());

    given(getOtherUserInfoUseCase.getOtherExtraProjects(userId, pageable)).willReturn(responsePage);
    given(otherUserReadWebMapper.toWebDto(project1)).willReturn(webProject1);
    given(otherUserReadWebMapper.toWebDto(project2)).willReturn(webProject2);

    // when & then
    mockMvc
        .perform(
            get("/api/v1/users/{userId}/projects", userId)
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_PROJECTS.getCode()))
        .andExpect(
            jsonPath("$.message").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_PROJECTS.getMessage()))
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[0].title").value("Project 1"));

    then(getOtherUserInfoUseCase).should().getOtherExtraProjects(userId, pageable);
    then(otherUserReadWebMapper).should().toWebDto(project1);
    then(otherUserReadWebMapper).should().toWebDto(project2);
  }
}
