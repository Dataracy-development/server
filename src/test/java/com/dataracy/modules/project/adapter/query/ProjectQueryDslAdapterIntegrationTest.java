package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectQueryDslAdapterIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ReadProjectQueryDslAdapter readAdapter;

    @Autowired
    private SearchProjectQueryDslAdapter searchAdapter;

    @Autowired
    private ValidateProjectQueryDslAdapter validateAdapter;

    private ProjectEntity savedProject;
    private ProjectEntity deletedProject;
    private ProjectEntity parentProject;
    private ProjectEntity childProject;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        entityManager.createQuery("DELETE FROM ProjectDataEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM ProjectEntity").executeUpdate();
        entityManager.flush();

        // 간단한 테스트 데이터 생성
        savedProject = ProjectEntity.of(
                "테스트 프로젝트",
                1L, // topicId
                1L, // userId
                1L, // analysisPurposeId
                1L, // dataSourceId
                1L, // authorLevelId
                false, // isContinue
                null, // parentProject
                "테스트 내용",
                "https://example.com/thumbnail.jpg"
        );

        // 삭제된 프로젝트 생성
        deletedProject = ProjectEntity.of(
                "삭제된 프로젝트",
                1L, // topicId
                2L, // userId
                1L, // analysisPurposeId
                1L, // dataSourceId
                1L, // authorLevelId
                false, // isContinue
                null, // parentProject
                "삭제된 내용",
                "https://example.com/thumbnail2.jpg"
        );
        deletedProject.delete(); // 삭제 처리

        // 부모 프로젝트 생성
        parentProject = ProjectEntity.of(
                "부모 프로젝트",
                1L, // topicId
                3L, // userId
                1L, // analysisPurposeId
                1L, // dataSourceId
                1L, // authorLevelId
                false, // isContinue
                null, // parentProject
                "부모 내용",
                "https://example.com/thumbnail3.jpg"
        );

        // 자식 프로젝트 생성
        childProject = ProjectEntity.of(
                "자식 프로젝트",
                1L, // topicId
                4L, // userId
                1L, // analysisPurposeId
                1L, // dataSourceId
                1L, // authorLevelId
                false, // isContinue
                parentProject, // parentProject
                "자식 내용",
                "https://example.com/thumbnail4.jpg"
        );

        // 엔티티 저장
        entityManager.persist(savedProject);
        entityManager.persist(deletedProject);
        entityManager.persist(parentProject);
        entityManager.persist(childProject);
        entityManager.flush();

        // 프로젝트 데이터 연결
        ProjectDataEntity projectData = ProjectDataEntity.of(savedProject, 1L);
        entityManager.persist(projectData);
        entityManager.flush();
    }

    private ProjectEntity createProjectEntity(String title, Long userId, boolean isDeleted) {
        return createProjectEntity(title, userId, isDeleted, null);
    }

    private ProjectEntity createProjectEntity(String title, Long userId, boolean isDeleted, ProjectEntity parentProject) {
        return ProjectEntity.of(
                title,
                1L, // topicId
                userId,
                1L, // analysisPurposeId
                1L, // dataSourceId
                1L, // authorLevelId
                false, // isContinue
                parentProject,
                "테스트 내용",
                "https://example.com/thumbnail.jpg"
        );
    }

    @Nested
    @DisplayName("ReadProjectQueryDslAdapter 테스트")
    class ReadProjectQueryDslAdapterTest {

        @Test
        @DisplayName("findProjectById - 존재하는 프로젝트 조회 성공")
        void findProjectById_WhenProjectExists_ReturnsProject() {
            // when
            Optional<Project> result = readAdapter.findProjectById(savedProject.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("테스트 프로젝트");
            assertThat(result.get().getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("findProjectById - 삭제된 프로젝트는 조회되지 않음")
        void findProjectById_WhenProjectIsDeleted_ReturnsEmpty() {
            // when
            Optional<Project> result = readAdapter.findProjectById(deletedProject.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findProjectById - 존재하지 않는 프로젝트 조회")
        void findProjectById_WhenProjectNotExists_ReturnsEmpty() {
            // when
            Optional<Project> result = readAdapter.findProjectById(999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findProjectWithDataById - 프로젝트와 연결된 데이터 조회")
        void findProjectWithDataById_WhenProjectExists_ReturnsProjectWithData() {
            // when
            Optional<ProjectWithDataIdsResponse> result = readAdapter.findProjectWithDataById(savedProject.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().project().getTitle()).isEqualTo("테스트 프로젝트");
            assertThat(result.get().dataIds()).contains(1L);
        }

        @Test
        @DisplayName("findUserProjects - 사용자 프로젝트 목록 조회")
        void findUserProjects_WhenUserHasProjects_ReturnsProjects() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findUserProjects(1L, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트");
        }

        @Test
        @DisplayName("getPopularProjects - 인기 프로젝트 조회")
        void getPopularProjects_ReturnsPopularProjects() {
            // given
            int size = 10;

            // when
            List<Project> result = readAdapter.getPopularProjects(size);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3); // 삭제되지 않은 프로젝트 3개
        }
    }

    @Nested
    @DisplayName("SearchProjectQueryDslAdapter 테스트")
    class SearchProjectQueryDslAdapterTest {

        @Test
        @DisplayName("searchByFilters - 키워드로 프로젝트 검색")
        void searchByFilters_WithKeyword_ReturnsMatchingProjects() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    "테스트", "LATEST", null, null, null, null
            );
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.MOST_LIKED);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트");
        }

        @Test
        @DisplayName("searchByFilters - 빈 키워드로 모든 프로젝트 검색")
        void searchByFilters_WithEmptyKeyword_ReturnsAllProjects() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    null, "LATEST", null, null, null, null
            );
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.MOST_LIKED);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3); // 삭제되지 않은 프로젝트 3개
        }

        @Test
        @DisplayName("searchByFilters - 존재하지 않는 키워드로 검색")
        void searchByFilters_WithNonExistentKeyword_ReturnsEmpty() {
            // given
            FilteringProjectRequest request = new FilteringProjectRequest(
                    "존재하지않는프로젝트", "LATEST", null, null, null, null
            );
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = searchAdapter.searchByFilters(request, pageable, ProjectSortType.MOST_LIKED);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("ValidateProjectQueryDslAdapter 테스트")
    class ValidateProjectQueryDslAdapterTest {

        @Test
        @DisplayName("checkParentProjectExistsById - 자식 프로젝트가 있는 부모 프로젝트")
        void checkParentProjectExistsById_WhenHasChildProjects_ReturnsTrue() {
            // when
            boolean result = validateAdapter.checkParentProjectExistsById(parentProject.getId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("checkParentProjectExistsById - 자식 프로젝트가 없는 프로젝트")
        void checkParentProjectExistsById_WhenNoChildProjects_ReturnsFalse() {
            // when
            boolean result = validateAdapter.checkParentProjectExistsById(savedProject.getId());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("checkProjectDataExistsByProjectId - 데이터가 연결된 프로젝트")
        void checkProjectDataExistsByProjectId_WhenHasData_ReturnsTrue() {
            // when
            boolean result = validateAdapter.checkProjectDataExistsByProjectId(savedProject.getId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("checkProjectDataExistsByProjectId - 데이터가 연결되지 않은 프로젝트")
        void checkProjectDataExistsByProjectId_WhenNoData_ReturnsFalse() {
            // when
            boolean result = validateAdapter.checkProjectDataExistsByProjectId(parentProject.getId());

            // then
            assertThat(result).isFalse();
        }
    }
}
