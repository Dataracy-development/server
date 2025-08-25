package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.mapper.search.FilteredProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchFilteredProjectsPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchRealTimeProjectsPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchSimilarProjectsPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProjectSearchServiceTest {

    @Mock SearchSimilarProjectsPort searchSimilarProjectsPort;
    @Mock SearchFilteredProjectsPort searchFilteredProjectsPort;
    @Mock SearchRealTimeProjectsPort searchRealTimeProjectsPort;
    @Mock FindProjectPort findProjectPort;
    @Mock FilteredProjectDtoMapper filteredProjectDtoMapper;
    @Mock FindProjectLabelMapUseCase findProjectLabelMapUseCase;
    @Mock FindUsernameUseCase findUsernameUseCase;

    @InjectMocks ProjectSearchService service;

    @Test
    @DisplayName("searchSimilarProjects - 기준 프로젝트 존재시 정상 반환")
    void searchSimilarProjects_success() {
        // given
        Long projectId = 1L;
        Project baseProject = Project.builder()
                .id(projectId).title("Base Project").content("기준 프로젝트")
                .topicId(10L).analysisPurposeId(20L).dataSourceId(30L).authorLevelId(40L)
                .build();
        given(findProjectPort.findProjectById(projectId)).willReturn(Optional.of(baseProject));
        given(searchSimilarProjectsPort.searchSimilarProjects(baseProject, 3)).willReturn(
                List.of(new SimilarProjectResponse(
                        2L, "유사 프로젝트", "내용", "userA", "thumb.png",
                        "Topic", "Purpose", "Source", "Author",
                        5L, 10L, 15L))
        );

        // when
        List<SimilarProjectResponse> result = service.searchSimilarProjects(projectId, 3);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("유사 프로젝트");
        then(searchSimilarProjectsPort).should().searchSimilarProjects(baseProject, 3);
    }

    @Test
    @DisplayName("searchSimilarProjects - 기준 프로젝트 없으면 예외 발생")
    void searchSimilarProjects_notFound_shouldThrow() {
        // given
        given(findProjectPort.findProjectById(99L)).willReturn(Optional.empty());

        // when
        ProjectException ex = catchThrowableOfType(() -> service.searchSimilarProjects(99L, 3), ProjectException.class);

        // then
        assertThat(ex).isNotNull();
    }

    @Test
    @DisplayName("searchByFilters - 라벨 매핑 및 자식 유저명 매핑 포함")
    void searchByFilters_success() {
        // given
        FilteringProjectRequest req = new FilteringProjectRequest("데이터", "LATEST", 10L, 20L, 30L, 40L);
        PageRequest pageable = PageRequest.of(0, 10);

        Project child = Project.builder().id(2L).userId(200L).build();
        Project parent = Project.builder()
                .id(1L).userId(100L).title("Parent Project").content("부모 내용")
                .topicId(10L).analysisPurposeId(20L).dataSourceId(30L).authorLevelId(40L)
                .childProjects(List.of(child))
                .build();

        Page<Project> projectPage = new PageImpl<>(List.of(parent));
        given(searchFilteredProjectsPort.searchByFilters(eq(req), eq(pageable), eq(ProjectSortType.LATEST)))
                .willReturn(projectPage);
        given(findProjectLabelMapUseCase.labelMapping(projectPage.getContent())).willReturn(
                new ProjectLabelMapResponse(
                        Map.of(100L, "parentUser"),
                        Map.of(10L, "TopicLabel"),
                        Map.of(20L, "PurposeLabel"),
                        Map.of(30L, "SourceLabel"),
                        Map.of(40L, "AuthorLabel")
                )
        );
        given(findUsernameUseCase.findUsernamesByIds(List.of(200L)))
                .willReturn(Map.of(200L, "childUser"));

        given(filteredProjectDtoMapper.toResponseDto(
                any(Project.class),
                eq("parentUser"), eq("TopicLabel"), eq("PurposeLabel"),
                eq("SourceLabel"), eq("AuthorLabel"),
                eq(Map.of(200L, "childUser"))
        )).willReturn(new FilteredProjectResponse(
                1L, "Parent Project", "부모 내용", "parentUser",
                "thumb.png", "TopicLabel", "PurposeLabel", "SourceLabel", "AuthorLabel",
                5L, 10L, 20L, LocalDateTime.now(), List.of()
        ));

        // when
        Page<FilteredProjectResponse> res = service.searchByFilters(req, pageable);

        // then
        assertThat(res.getContent()).hasSize(1);
        assertThat(res.getContent().get(0).title()).isEqualTo("Parent Project");
        then(findUsernameUseCase).should().findUsernamesByIds(List.of(200L));
    }

    @Test
    @DisplayName("searchByKeyword - 키워드가 유효할 때 결과 반환")
    void searchByKeyword_success() {
        // given
        String keyword = "AI";
        given(searchRealTimeProjectsPort.searchByKeyword(keyword, 5)).willReturn(
                List.of(new RealTimeProjectResponse(1L, "AI Project", "userA", "thumb.png"))
        );

        // when
        List<RealTimeProjectResponse> res = service.searchByKeyword(keyword, 5);

        // then
        assertThat(res).hasSize(1);
        assertThat(res.get(0).title()).isEqualTo("AI Project");
        then(searchRealTimeProjectsPort).should().searchByKeyword(keyword, 5);
    }

    @Test
    @DisplayName("searchByKeyword - 키워드가 공백/Null이면 빈 리스트 반환")
    void searchByKeyword_blankOrNull_shouldReturnEmpty() {
        // when
        List<RealTimeProjectResponse> resBlank = service.searchByKeyword("   ", 5);
        List<RealTimeProjectResponse> resNull = service.searchByKeyword(null, 5);

        // then
        assertThat(resBlank).isEmpty();
        assertThat(resNull).isEmpty();
        then(searchRealTimeProjectsPort).shouldHaveNoInteractions();
    }
}
