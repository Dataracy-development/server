package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SearchProjectQueryDslAdapterIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SearchProjectQueryDslAdapter searchAdapter;

    private ProjectEntity savedProject;
    private ProjectEntity deletedProject;
    private ProjectEntity childProject;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        entityManager.createQuery("DELETE FROM ProjectEntity").executeUpdate();
        entityManager.flush();

        // 테스트 데이터 생성
        savedProject = ProjectEntity.builder()
                .title("테스트 프로젝트")
                .content("테스트 내용")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .analysisPurposeId(1L)
                .authorLevelId(1L)
                .isContinue(false)
                .isDeleted(false)
                .build();
        entityManager.persist(savedProject);

        deletedProject = ProjectEntity.builder()
                .title("삭제된 프로젝트")
                .content("삭제된 내용")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .analysisPurposeId(1L)
                .authorLevelId(1L)
                .isContinue(false)
                .isDeleted(true)
                .build();
        entityManager.persist(deletedProject);

        childProject = ProjectEntity.builder()
                .title("자식 프로젝트")
                .content("자식 내용")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .analysisPurposeId(1L)
                .authorLevelId(1L)
                .isContinue(false)
                .isDeleted(false)
                .parentProject(savedProject)
                .build();
        entityManager.persist(childProject);

        entityManager.flush();
    }

    @Nested
    @DisplayName("SearchFilteredProjectsPort 테스트")
    class SearchFilteredProjectsPortTest {

        @Test
        @DisplayName("키워드로 프로젝트 검색")
        void searchByFilters_키워드로_프로젝트_검색_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    "테스트", null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트");
        }

        @Test
        @DisplayName("빈 키워드로 모든 프로젝트 검색")
        void searchByFilters_빈_키워드로_모든_프로젝트_검색_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2); // savedProject, childProject (deletedProject 제외)
        }

        @Test
        @DisplayName("삭제된 프로젝트는 검색 결과에서 제외")
        void searchByFilters_삭제된_프로젝트_검색_결과에서_제외() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    "삭제", null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("정렬 조건별 프로젝트 검색")
        void searchByFilters_정렬_조건별_프로젝트_검색_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when & then
            Page<Project> latestResult = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);
            assertThat(latestResult).isNotEmpty();

            Page<Project> popularResult = searchAdapter.searchByFilters(request, pageable, ProjectSortType.MOST_LIKED);
            assertThat(popularResult).isNotEmpty();

            Page<Project> viewResult = searchAdapter.searchByFilters(request, pageable, ProjectSortType.MOST_VIEWED);
            assertThat(viewResult).isNotEmpty();
        }

        @Test
        @DisplayName("페이지네이션 검색")
        void searchByFilters_페이지네이션_검색_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 1);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("두 번째 페이지 검색")
        void searchByFilters_두_번째_페이지_검색_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(1, 1);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("토픽 ID로 프로젝트 필터링")
        void searchByFilters_토픽_ID로_프로젝트_필터링_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, 1L, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 토픽 ID로 필터링 시 빈 결과")
        void searchByFilters_존재하지_않는_토픽_ID_필터링_시_빈_결과() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, 999L, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("분석 목적 ID로 프로젝트 필터링")
        void searchByFilters_분석_목적_ID로_프로젝트_필터링_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, 1L, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("데이터 소스 ID로 프로젝트 필터링")
        void searchByFilters_데이터_소스_ID로_프로젝트_필터링_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, null, 1L, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("저자 레벨 ID로 프로젝트 필터링")
        void searchByFilters_저자_레벨_ID로_프로젝트_필터링_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, null, null, 1L);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("복합 필터 조건으로 프로젝트 검색")
        void searchByFilters_복합_필터_조건으로_프로젝트_검색_성공() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    "테스트", null, 1L, 1L, 1L, 1L);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트");
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("부모-자식 프로젝트 관계 검색")
        void parent_child_project_relationship_search() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
            
            // 부모 프로젝트와 자식 프로젝트가 모두 검색되는지 확인
            List<String> titles = result.getContent().stream()
                    .map(Project::getTitle)
                    .toList();
            assertThat(titles).contains("테스트 프로젝트", "자식 프로젝트");
        }

        @Test
        @DisplayName("빈 검색 결과 처리")
        void empty_search_result_handling() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    "존재하지않는키워드", null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.LATEST);

            // then
            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getTotalPages()).isZero();
        }
    }
}
