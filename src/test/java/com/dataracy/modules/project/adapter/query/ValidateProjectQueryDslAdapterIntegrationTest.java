package com.dataracy.modules.project.adapter.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ValidateProjectQueryDslAdapterIntegrationTest {

  @PersistenceContext private EntityManager entityManager;

  @Autowired private ValidateProjectQueryDslAdapter validateAdapter;

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
    parentProject =
        ProjectEntity.builder()
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

    childProject =
        ProjectEntity.builder()
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

    deletedChildProject =
        ProjectEntity.builder()
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
    void checkParentProjectExistsByIdWhenHasChildrenReturnsTrue() {
      // when
      boolean result = validateAdapter.checkParentProjectExistsById(parentProject.getId());

      // then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("부모 프로젝트 ID로 자식 프로젝트 존재 확인 - 자식이 없는 경우")
    void checkParentProjectExistsByIdWhenNoChildrenReturnsFalse() {
      // when
      boolean result = validateAdapter.checkParentProjectExistsById(childProject.getId());

      // then
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("삭제된 자식 프로젝트는 존재하지 않는 것으로 간주")
    void checkParentProjectExistsByIdWhenDeletedChildrenNotExists() {
      // given - 삭제된 자식 프로젝트만 있는 부모 프로젝트 생성
      ProjectEntity anotherParent =
          ProjectEntity.builder()
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

      ProjectEntity deletedChild =
          ProjectEntity.builder()
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
    void checkParentProjectExistsByIdWithNonExistentIdReturnsFalse() {
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
    void checkProjectDataExistsByProjectIdWhenDataExistsReturnsTrue() {
      // when
      boolean result = validateAdapter.checkProjectDataExistsByProjectId(parentProject.getId());

      // then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("프로젝트 ID로 프로젝트 데이터 존재 확인 - 데이터가 없는 경우")
    void checkProjectDataExistsByProjectIdWhenNoDataReturnsFalse() {
      // when
      boolean result = validateAdapter.checkProjectDataExistsByProjectId(childProject.getId());

      // then
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("프로젝트 데이터가 있는 경우 true 반환")
    void checkProjectDataExistsByProjectIdWhenProjectDataExistsReturnsTrue() {
      // given - 새로운 프로젝트와 데이터 생성
      ProjectEntity anotherProject =
          ProjectEntity.builder()
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

      ProjectDataEntity anotherProjectData =
          ProjectDataEntity.builder().project(anotherProject).dataId(2L).build();
      entityManager.persist(anotherProjectData);
      entityManager.flush();

      // when
      boolean result = validateAdapter.checkProjectDataExistsByProjectId(anotherProject.getId());

      // then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 ID로 확인 시 false 반환")
    void checkProjectDataExistsByProjectIdWithNonExistentIdReturnsFalse() {
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
    void multiplechildrenparentproject() {
      // given - 추가 자식 프로젝트 생성
      ProjectEntity secondChild =
          ProjectEntity.builder()
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
      assertAll(() -> assertThat(hasChildren).isTrue(), () -> assertThat(hasData).isTrue());
    }

    @Test
    @DisplayName("자식 프로젝트와 데이터가 모두 없는 프로젝트")
    void projectWithoutChildrenAndData() {
      // given - 독립적인 프로젝트 생성
      ProjectEntity independentProject =
          ProjectEntity.builder()
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
      boolean hasChildren =
          validateAdapter.checkParentProjectExistsById(independentProject.getId());
      boolean hasData =
          validateAdapter.checkProjectDataExistsByProjectId(independentProject.getId());

      // then
      assertAll(() -> assertThat(hasChildren).isFalse(), () -> assertThat(hasData).isFalse());
    }

    @Test
    @DisplayName("삭제된 프로젝트에 대한 검증")
    void validationfordeletedproject() {
      // given - 삭제된 프로젝트 생성
      ProjectEntity deletedProject =
          ProjectEntity.builder()
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

      ProjectDataEntity deletedProjectData =
          ProjectDataEntity.builder().project(deletedProject).dataId(3L).build();
      entityManager.persist(deletedProjectData);
      entityManager.flush();

      // when
      boolean hasChildren = validateAdapter.checkParentProjectExistsById(deletedProject.getId());
      boolean hasData = validateAdapter.checkProjectDataExistsByProjectId(deletedProject.getId());

      // then - 삭제된 프로젝트는 검증에서 제외되므로 false
      assertAll(() -> assertThat(hasChildren).isFalse(), () -> assertThat(hasData).isFalse());
    }
  }
}
