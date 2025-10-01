package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
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
import static org.junit.jupiter.api.Assertions.assertAll;

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
        entityManager.createQuery("DELETE FROM LikeEntity").executeUpdate();
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

        // 좋아요 데이터 생성 (findLikeProjects 테스트용)
        LikeEntity likeEntity = LikeEntity.of(savedProject.getId(), TargetType.PROJECT, 1L);
        entityManager.persist(likeEntity);
        
        entityManager.flush();
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
            assertAll(
                    () -> assertThat(result).isPresent(),
                    () -> assertThat(result.get().getTitle()).isEqualTo("테스트 프로젝트"),
                    () -> assertThat(result.get().getUserId()).isEqualTo(1L)
            );
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
            assertAll(
                    () -> assertThat(result).isPresent(),
                    () -> assertThat(result.get().project().getTitle()).isEqualTo("테스트 프로젝트"),
                    () -> assertThat(result.get().dataIds()).contains(1L)
            );
        }

        @Test
        @DisplayName("findUserProjects - 사용자 프로젝트 목록 조회")
        void findUserProjects_WhenUserHasProjects_ReturnsProjects() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findUserProjects(1L, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(1),
                    () -> assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트")
            );
        }

        @Test
        @DisplayName("getPopularProjects - 인기 프로젝트 조회")
        void getPopularProjects_ReturnsPopularProjects() {
            // given
            int size = 10;

            // when
            List<Project> result = readAdapter.getPopularProjects(size);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(3) // 삭제되지 않은 프로젝트 3개
            );
        }

        @Test
        @DisplayName("findContinuedProjects - 이어가기 프로젝트 목록 조회")
        void findContinuedProjects_WhenParentHasChildren_ReturnsChildren() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findContinuedProjects(parentProject.getId(), pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(1),
                    () -> assertThat(result.getContent().get(0).getTitle()).isEqualTo("자식 프로젝트"),
                    () -> assertThat(result.getTotalElements()).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("findContinuedProjects - 자식 프로젝트가 없는 경우")
        void findContinuedProjects_WhenNoChildren_ReturnsEmpty() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findContinuedProjects(savedProject.getId(), pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isZero()
            );
        }

        @Test
        @DisplayName("findConnectedProjectsAssociatedWithDataset - 데이터셋과 연결된 프로젝트 조회")
        void findConnectedProjectsAssociatedWithDataset_WhenDataConnected_ReturnsProjects() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findConnectedProjectsAssociatedWithDataset(1L, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(1),
                    () -> assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트"),
                    () -> assertThat(result.getTotalElements()).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("findConnectedProjectsAssociatedWithDataset - 연결된 프로젝트가 없는 경우")
        void findConnectedProjectsAssociatedWithDataset_WhenNoConnectedProjects_ReturnsEmpty() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findConnectedProjectsAssociatedWithDataset(999L, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isZero()
            );
        }

        @Test
        @DisplayName("findUserProjects - null Pageable 처리")
        void findUserProjects_WithNullPageable_UsesDefaultPageable() {
            // when
            Page<Project> result = readAdapter.findUserProjects(1L, null);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(1),
                    () -> assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트"),
                    () -> assertThat(result.getPageable().getPageSize()).isEqualTo(5) // 기본 페이지 크기
            );
        }

        @Test
        @DisplayName("findUserProjects - 프로젝트가 없는 사용자")
        void findUserProjects_WhenUserHasNoProjects_ReturnsEmpty() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findUserProjects(999L, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isZero()
            );
        }

        @Test
        @DisplayName("findProjectWithDataById - 존재하지 않는 프로젝트")
        void findProjectWithDataById_WhenProjectNotExists_ReturnsEmpty() {
            // when
            Optional<ProjectWithDataIdsResponse> result = readAdapter.findProjectWithDataById(999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findProjectWithDataById - 데이터가 연결되지 않은 프로젝트")
        void findProjectWithDataById_WhenNoDataConnected_ReturnsProjectWithEmptyData() {
            // when
            Optional<ProjectWithDataIdsResponse> result = readAdapter.findProjectWithDataById(parentProject.getId());

            // then
            assertAll(
                    () -> assertThat(result).isPresent(),
                    () -> assertThat(result.get().project().getTitle()).isEqualTo("부모 프로젝트"),
                    () -> assertThat(result.get().dataIds()).isEmpty()
            );
        }

        @Test
        @DisplayName("findLikeProjects - 사용자가 좋아요한 프로젝트 조회")
        void findLikeProjects_WhenUserHasLikes_ReturnsLikedProjects() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findLikeProjects(1L, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(1),
                    () -> assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트"),
                    () -> assertThat(result.getTotalElements()).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("findLikeProjects - 좋아요가 없는 사용자")
        void findLikeProjects_WhenUserHasNoLikes_ReturnsEmpty() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Project> result = readAdapter.findLikeProjects(999L, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isZero()
            );
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
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(1),
                    () -> assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프로젝트")
            );
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
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(3) // 삭제되지 않은 프로젝트 3개
            );
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
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).isEmpty()
            );
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
