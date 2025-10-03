/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.web.api.search;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProjectSearchControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProjectSearchWebMapper projectSearchWebMapper;

  @MockBean private ProjectFilterWebMapper projectFilterWebMapper;

  @MockBean private SearchRealTimeProjectsUseCase searchRealTimeProjectsUseCase;

  @MockBean private SearchSimilarProjectsUseCase searchSimilarProjectsUseCase;

  @MockBean private SearchFilteredProjectsUseCase searchFilteredProjectsUseCase;

  // 공통 모킹
  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
  @MockBean private JwtValidateUseCase jwtValidateUseCase;
  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;
  @MockBean private com.dataracy.modules.common.logging.ApiLogger apiLogger;

  @Test
  @DisplayName("searchRealTimeProjects API: 성공 - 200 OK와 JSON 응답 검증")
  void searchRealTimeProjectsSuccess() throws Exception {
    // given
    String keyword = "AI";
    Integer size = 10;

    RealTimeProjectResponse response1 =
        new RealTimeProjectResponse(1L, "AI Project 1", 1L, "User 1", "profile1.jpg", "thumb1.jpg");
    RealTimeProjectResponse response2 =
        new RealTimeProjectResponse(2L, "AI Project 2", 2L, "User 2", "profile2.jpg", "thumb2.jpg");
    List<RealTimeProjectResponse> responseList = List.of(response1, response2);

    RealTimeProjectWebResponse webResponse1 =
        new RealTimeProjectWebResponse(
            1L, "AI Project 1", 1L, "User 1", "profile1.jpg", "thumb1.jpg");
    RealTimeProjectWebResponse webResponse2 =
        new RealTimeProjectWebResponse(
            2L, "AI Project 2", 2L, "User 2", "profile2.jpg", "thumb2.jpg");

    given(searchRealTimeProjectsUseCase.searchByKeyword(keyword, size)).willReturn(responseList);
    given(projectSearchWebMapper.toWebDto(response1)).willReturn(webResponse1);
    given(projectSearchWebMapper.toWebDto(response2)).willReturn(webResponse2);

    // when & then
    mockMvc
        .perform(
            get("/api/v1/projects/search/real-time")
                .param("keyword", keyword)
                .param("size", String.valueOf(size))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS.getCode()))
        .andExpect(
            jsonPath("$.message").value(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS.getMessage()))
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].title").value("AI Project 1"));

    then(searchRealTimeProjectsUseCase).should().searchByKeyword(keyword, size);
    then(projectSearchWebMapper).should().toWebDto(response1);
    then(projectSearchWebMapper).should().toWebDto(response2);
  }

  @Test
  @DisplayName("searchSimilarProjects API: 성공 - 200 OK와 JSON 응답 검증")
  void searchSimilarProjectsSuccess() throws Exception {
    // given
    Long projectId = 1L;
    Integer size = 10;

    SimilarProjectResponse response1 =
        new SimilarProjectResponse(
            2L,
            "Similar Project 1",
            "Content 1",
            1L,
            "User 1",
            "profile1.jpg",
            "thumb1.jpg",
            "AI",
            "Research",
            "Public",
            "Expert",
            10L,
            5L,
            100L);
    SimilarProjectResponse response2 =
        new SimilarProjectResponse(
            3L,
            "Similar Project 2",
            "Content 2",
            2L,
            "User 2",
            "profile2.jpg",
            "thumb2.jpg",
            "AI",
            "Research",
            "Public",
            "Expert",
            20L,
            10L,
            200L);
    List<SimilarProjectResponse> responseList = List.of(response1, response2);

    SimilarProjectWebResponse webResponse1 =
        new SimilarProjectWebResponse(
            2L,
            "Similar Project 1",
            "Content 1",
            1L,
            "User 1",
            "profile1.jpg",
            "thumb1.jpg",
            "AI",
            "Research",
            "Public",
            "Expert",
            10L,
            5L,
            100L);
    SimilarProjectWebResponse webResponse2 =
        new SimilarProjectWebResponse(
            3L,
            "Similar Project 2",
            "Content 2",
            2L,
            "User 2",
            "profile2.jpg",
            "thumb2.jpg",
            "AI",
            "Research",
            "Public",
            "Expert",
            20L,
            10L,
            200L);

    given(searchSimilarProjectsUseCase.searchSimilarProjects(projectId, size))
        .willReturn(responseList);
    given(projectSearchWebMapper.toWebDto(response1)).willReturn(webResponse1);
    given(projectSearchWebMapper.toWebDto(response2)).willReturn(webResponse2);

    // when & then
    mockMvc
        .perform(
            get("/api/v1/projects/{projectId}/similar", projectId)
                .param("size", String.valueOf(size))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS.getCode()))
        .andExpect(
            jsonPath("$.message").value(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS.getMessage()))
        .andExpect(jsonPath("$.data[0].id").value(2))
        .andExpect(jsonPath("$.data[0].title").value("Similar Project 1"));

    then(searchSimilarProjectsUseCase).should().searchSimilarProjects(projectId, size);
    then(projectSearchWebMapper).should().toWebDto(response1);
    then(projectSearchWebMapper).should().toWebDto(response2);
  }

  @Test
  @DisplayName("searchFilteredProjects API: 성공 - 200 OK와 JSON 응답 검증")
  void searchFilteredProjectsSuccess() throws Exception {
    // given
    FilteringProjectRequest requestDto =
        new FilteringProjectRequest("AI", "Research", 1L, 2L, 3L, 4L);
    Pageable pageable = PageRequest.of(0, 10);

    FilteredProjectResponse response1 =
        new FilteredProjectResponse(
            1L,
            "Filtered Project 1",
            "Content 1",
            1L,
            "User 1",
            "profile1.jpg",
            "thumb1.jpg",
            "AI",
            "Research",
            "Public",
            "Expert",
            10L,
            5L,
            100L,
            LocalDateTime.now(),
            List.of());
    FilteredProjectResponse response2 =
        new FilteredProjectResponse(
            2L,
            "Filtered Project 2",
            "Content 2",
            2L,
            "User 2",
            "profile2.jpg",
            "thumb2.jpg",
            "DATA",
            "Research",
            "Public",
            "Expert",
            20L,
            10L,
            200L,
            LocalDateTime.now(),
            List.of());
    Page<FilteredProjectResponse> responsePage =
        new PageImpl<>(List.of(response1, response2), pageable, 2);

    FilteredProjectWebResponse webResponse1 =
        new FilteredProjectWebResponse(
            1L,
            "Filtered Project 1",
            "Content 1",
            1L,
            "User 1",
            "profile1.jpg",
            "thumb1.jpg",
            "AI",
            "Research",
            "Public",
            "Expert",
            10L,
            5L,
            100L,
            LocalDateTime.now(),
            List.of());
    FilteredProjectWebResponse webResponse2 =
        new FilteredProjectWebResponse(
            2L,
            "Filtered Project 2",
            "Content 2",
            2L,
            "User 2",
            "profile2.jpg",
            "thumb2.jpg",
            "DATA",
            "Research",
            "Public",
            "Expert",
            20L,
            10L,
            200L,
            LocalDateTime.now(),
            List.of());

    given(projectFilterWebMapper.toApplicationDto(any(FilteringProjectWebRequest.class)))
        .willReturn(requestDto);
    given(
            searchFilteredProjectsUseCase.searchByFilters(
                any(FilteringProjectRequest.class), any(Pageable.class)))
        .willReturn(responsePage);
    given(projectFilterWebMapper.toWebDto(response1)).willReturn(webResponse1);
    given(projectFilterWebMapper.toWebDto(response2)).willReturn(webResponse2);

    // when & then
    mockMvc
        .perform(
            get("/api/v1/projects/filter")
                .param("keyword", "AI")
                .param("analysisPurpose", "Research")
                .param("topicId", "1")
                .param("dataSourceId", "2")
                .param("authorLevelId", "3")
                .param("occupationId", "4")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ProjectSuccessStatus.FIND_FILTERED_PROJECTS.getCode()))
        .andExpect(
            jsonPath("$.message").value(ProjectSuccessStatus.FIND_FILTERED_PROJECTS.getMessage()))
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[0].title").value("Filtered Project 1"));

    then(searchFilteredProjectsUseCase).should().searchByFilters(requestDto, pageable);
    then(projectFilterWebMapper).should().toWebDto(response1);
    then(projectFilterWebMapper).should().toWebDto(response2);
  }
}
