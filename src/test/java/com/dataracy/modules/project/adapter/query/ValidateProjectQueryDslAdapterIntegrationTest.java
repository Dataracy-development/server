package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ValidateProjectQueryDslAdapterIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ValidateProjectQueryDslAdapter validateAdapter;

    private ProjectEntity parentProject;
    private ProjectEntity childProject;
    private ProjectEntity deletedChildProject;
    private ProjectDataEntity projectData;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        entityManager.createQuery("DELETE FROM ProjectDataEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM ProjectEntity").executeUpdate();
        entityManager.flush();

        // 테스트 데이터 생성
        parentProject = ProjectEntity.builder()
                .title("부모 프로젝트")
                .content("부모 내용")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .analysisPurposeId(1L)
                .authorLevelId(1L)
                .isContinue(false)
                .isDeleted(false)
                .build();
        entityManager.persist(parentProject);

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
                .parentProject(parentProject)
                .build();
        entityManager.persist(childProject);

        deletedChildProject = ProjectEntity.builder()
                .title("삭제된 자식 프로젝트")
                .content("삭제된 자식 내용")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .analysisPurposeId(1L)
                .authorLevelId(1L)
                .isContinue(false)
                .isDeleted(true)
                .parentProject(parentProject)
                .build();
        entityManager.persist(deletedChildProject);

        // 프로젝트 데이터 연결
        projectData = ProjectDataEntity.of(parentProject, 1L);
        entityManager.persist(projectData);

        entityManager.flush();
    }

    @Nested
    @DisplayName("CheckProjectExistsByParentPort 테스트")
    class CheckProjectExistsByParentPortTest {

        @Test
        @DisplayName("부모 프로젝트 ID로 자식 프로젝트 존재 확인 - 자식이 있는 경우")
        void checkParentProjectExistsById_자식이_있는_경우_true_반환() {
            // when
            boolean result = validateAdapter.checkParentProjectExistsById(parentProject.getId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("부모 프로젝트 ID로 자식 프로젝트 존재 확인 - 자식이 없는 경우")
        void checkParentProjectExistsById_자식이_없는_경우_false_반환() {
            // when
            boolean result = validateAdapter.checkParentProjectExistsById(childProject.getId());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("삭제된 자식 프로젝트는 존재하지 않는 것으로 간주")
        void checkParentProjectExistsById_삭제된_자식_프로젝트_존재하지_않음() {
            // given - 삭제된 자식 프로젝트만 있는 부모 프로젝트 생성
            ProjectEntity anotherParent = ProjectEntity.builder()
                    .title("다른 부모 프로젝트")
                    .content("다른 부모 내용")
                    .userId(1L)
                    .topicId(1L)
                    .dataSourceId(1L)
                    .analysisPurposeId(1L)
                    .authorLevelId(1L)
                    .isContinue(false)
                    .isDeleted(false)
                    .build();
            entityManager.persist(anotherParent);

            ProjectEntity deletedChild = ProjectEntity.builder()
                    .title("삭제된 자식")
                    .content("삭제된 자식 내용")
                    .userId(1L)
                    .topicId(1L)
                    .dataSourceId(1L)
                    .analysisPurposeId(1L)
                    .authorLevelId(1L)
                    .isContinue(false)
                    .isDeleted(true)
                    .parentProject(anotherParent)
                    .build();
            entityManager.persist(deletedChild);
            entityManager.flush();

            // when
            boolean result = validateAdapter.checkParentProjectExistsById(anotherParent.getId());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID로 확인 시 false 반환")
        void checkParentProjectExistsById_존재하지_않는_프로젝트_ID_false_반환() {
            // when
            boolean result = validateAdapter.checkParentProjectExistsById(999L);

            // then
            assertThat(result).isFalse();
        }

    }

    @Nested
    @DisplayName("CheckProjectDataExistsPort 테스트")
    class CheckProjectDataExistsPortTest {

        @Test
        @DisplayName("프로젝트 ID로 프로젝트 데이터 존재 확인 - 데이터가 있는 경우")
        void checkProjectDataExistsByProjectId_데이터가_있는_경우_true_반환() {
            // when
            boolean result = validateAdapter.checkProjectDataExistsByProjectId(parentProject.getId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("프로젝트 ID로 프로젝트 데이터 존재 확인 - 데이터가 없는 경우")
        void checkProjectDataExistsByProjectId_데이터가_없는_경우_false_반환() {
            // when
            boolean result = validateAdapter.checkProjectDataExistsByProjectId(childProject.getId());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("프로젝트 데이터가 있는 경우 true 반환")
        void checkProjectDataExistsByProjectId_프로젝트_데이터가_있는_경우_true_반환() {
            // given - 새로운 프로젝트와 데이터 생성
            ProjectEntity anotherProject = ProjectEntity.builder()
                    .title("다른 프로젝트")
                    .content("다른 내용")
                    .userId(1L)
                    .topicId(1L)
                    .dataSourceId(1L)
                    .analysisPurposeId(1L)
                    .authorLevelId(1L)
                    .isContinue(false)
                    .isDeleted(false)
                    .build();
            entityManager.persist(anotherProject);

            ProjectDataEntity anotherProjectData = ProjectDataEntity.builder()
                    .project(anotherProject)
                    .dataId(2L)
                    .build();
            entityManager.persist(anotherProjectData);
            entityManager.flush();

            // when
            boolean result = validateAdapter.checkProjectDataExistsByProjectId(anotherProject.getId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID로 확인 시 false 반환")
        void checkProjectDataExistsByProjectId_존재하지_않는_프로젝트_ID_false_반환() {
            // when
            boolean result = validateAdapter.checkProjectDataExistsByProjectId(999L);

            // then
            assertThat(result).isFalse();
        }

    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("여러 자식 프로젝트가 있는 부모 프로젝트")
        void multiple_children_parent_project() {
            // given - 추가 자식 프로젝트 생성
            ProjectEntity secondChild = ProjectEntity.builder()
                    .title("두 번째 자식 프로젝트")
                    .content("두 번째 자식 내용")
                    .userId(1L)
                    .topicId(1L)
                    .dataSourceId(1L)
                    .analysisPurposeId(1L)
                    .authorLevelId(1L)
                    .isContinue(false)
                    .isDeleted(false)
                    .parentProject(parentProject)
                    .build();
            entityManager.persist(secondChild);
            entityManager.flush();

            // when
            boolean hasChildren = validateAdapter.checkParentProjectExistsById(parentProject.getId());
            boolean hasData = validateAdapter.checkProjectDataExistsByProjectId(parentProject.getId());

            // then
            assertThat(hasChildren).isTrue();
            assertThat(hasData).isTrue();
        }

        @Test
        @DisplayName("자식 프로젝트와 데이터가 모두 없는 프로젝트")
        void project_without_children_and_data() {
            // given - 독립적인 프로젝트 생성
            ProjectEntity independentProject = ProjectEntity.builder()
                    .title("독립 프로젝트")
                    .content("독립 내용")
                    .userId(1L)
                    .topicId(1L)
                    .dataSourceId(1L)
                    .analysisPurposeId(1L)
                    .authorLevelId(1L)
                    .isContinue(false)
                    .isDeleted(false)
                    .build();
            entityManager.persist(independentProject);
            entityManager.flush();

            // when
            boolean hasChildren = validateAdapter.checkParentProjectExistsById(independentProject.getId());
            boolean hasData = validateAdapter.checkProjectDataExistsByProjectId(independentProject.getId());

            // then
            assertThat(hasChildren).isFalse();
            assertThat(hasData).isFalse();
        }

        @Test
        @DisplayName("삭제된 프로젝트에 대한 검증")
        void validation_for_deleted_project() {
            // given - 삭제된 프로젝트 생성
            ProjectEntity deletedProject = ProjectEntity.builder()
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

            ProjectDataEntity deletedProjectData = ProjectDataEntity.builder()
                    .project(deletedProject)
                    .dataId(3L)
                    .build();
            entityManager.persist(deletedProjectData);
            entityManager.flush();

            // when
            boolean hasChildren = validateAdapter.checkParentProjectExistsById(deletedProject.getId());
            boolean hasData = validateAdapter.checkProjectDataExistsByProjectId(deletedProject.getId());

            // then - 삭제된 프로젝트는 검증에서 제외되므로 false
            assertThat(hasChildren).isFalse();
            assertThat(hasData).isFalse();
        }
    }
}
