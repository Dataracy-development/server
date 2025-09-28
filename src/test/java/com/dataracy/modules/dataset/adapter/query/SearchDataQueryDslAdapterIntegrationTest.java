package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SearchDataQueryDslAdapterIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SearchDataQueryDslAdapter searchAdapter;

    private DataEntity savedData;
    private DataEntity deletedData;
    private DataEntity popularData;
    private ProjectEntity savedProject;
    private ProjectDataEntity projectDataEntity;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        entityManager.createQuery("DELETE FROM ProjectDataEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM DataMetadataEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM DataEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM ProjectEntity").executeUpdate();
        entityManager.flush();

        // 간단한 테스트 데이터 생성 (하드코딩된 ID 사용)
        savedData = DataEntity.builder()
                .title("테스트 데이터")
                .description("테스트 설명")
                .analysisGuide("테스트 가이드")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .dataTypeId(1L)
                .isDeleted(false)
                .downloadCount(100)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();
        entityManager.persist(savedData);

        // 메타데이터 연결
        DataMetadataEntity metadata = DataMetadataEntity.builder()
                .data(savedData)
                .columnCount(10)
                .rowCount(1000)
                .build();
        entityManager.persist(metadata);

        deletedData = DataEntity.builder()
                .title("삭제된 데이터")
                .description("삭제된 설명")
                .analysisGuide("삭제된 가이드")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .dataTypeId(1L)
                .isDeleted(true)
                .downloadCount(50)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();
        entityManager.persist(deletedData);

        popularData = DataEntity.builder()
                .title("인기 데이터")
                .description("인기 설명")
                .analysisGuide("인기 가이드")
                .userId(1L)
                .topicId(1L)
                .dataSourceId(1L)
                .dataTypeId(1L)
                .isDeleted(false)
                .downloadCount(1000)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();
        entityManager.persist(popularData);

        // 프로젝트 생성
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

        // 프로젝트-데이터 연결
        projectDataEntity = ProjectDataEntity.of(savedProject, savedData.getId());
        entityManager.persist(projectDataEntity);

        entityManager.flush();
    }

    @Nested
    @DisplayName("SearchFilteredDataSetsPort 테스트")
    class SearchFilteredDataSetsPortTest {

        @Test
        @DisplayName("키워드로 데이터셋 검색")
        void searchByFilters_키워드로_데이터셋_검색_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    "테스트", null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).data().getTitle()).isEqualTo("테스트 데이터");
        }

        @Test
        @DisplayName("빈 키워드로 모든 데이터셋 검색")
        void searchByFilters_빈_키워드로_모든_데이터셋_검색_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2); // savedData, popularData (deletedData 제외)
        }

        @Test
        @DisplayName("삭제된 데이터는 검색 결과에서 제외")
        void searchByFilters_삭제된_데이터_검색_결과에서_제외() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    "삭제", null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("정렬 조건별 데이터셋 검색")
        void searchByFilters_정렬_조건별_데이터셋_검색_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when & then
            Page<DataWithProjectCountDto> latestResult = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);
            assertThat(latestResult).isNotEmpty();

            Page<DataWithProjectCountDto> downloadResult = searchAdapter.searchByFilters(request, pageable, DataSortType.DOWNLOAD);
            assertThat(downloadResult).isNotEmpty();
        }

        @Test
        @DisplayName("OLDEST 정렬 테스트")
        void searchByFilters_OLDEST_정렬_테스트() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.OLDEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("페이지네이션 검색")
        void searchByFilters_페이지네이션_검색_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 1);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("토픽 ID로 데이터셋 필터링")
        void searchByFilters_토픽_ID로_데이터셋_필터링_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, 1L, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 토픽 ID로 필터링 시 빈 결과")
        void searchByFilters_존재하지_않는_토픽_ID_필터링_시_빈_결과() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, 999L, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("데이터 소스 ID로 데이터셋 필터링")
        void searchByFilters_데이터_소스_ID로_데이터셋_필터링_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, 1L, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("데이터 타입 ID로 데이터셋 필터링")
        void searchByFilters_데이터_타입_ID로_데이터셋_필터링_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, null, 1L, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("연도로 데이터셋 필터링")
        void searchByFilters_연도로_데이터셋_필터링_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, null, null, 2024);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("복합 필터 조건으로 데이터셋 검색")
        void searchByFilters_복합_필터_조건으로_데이터셋_검색_성공() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    "테스트", null, 1L, 1L, 1L, 2024);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).data().getTitle()).isEqualTo("테스트 데이터");
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("다중 프로젝트 연결 데이터 테스트")
        void data_with_multiple_project_connections() {
            // given - 추가 데이터와 프로젝트 생성
            DataEntity anotherData = DataEntity.builder()
                    .title("다른 데이터")
                    .description("다른 설명")
                    .analysisGuide("다른 가이드")
                    .userId(1L)
                    .topicId(1L)
                    .dataSourceId(1L)
                    .dataTypeId(1L)
                    .isDeleted(false)
                    .downloadCount(200)
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2024, 12, 31))
                    .build();
            entityManager.persist(anotherData);

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

            // anotherData를 두 프로젝트에 연결
            ProjectDataEntity projectData1 = ProjectDataEntity.of(savedProject, anotherData.getId());
            entityManager.persist(projectData1);
            
            ProjectDataEntity projectData2 = ProjectDataEntity.of(anotherProject, anotherData.getId());
            entityManager.persist(projectData2);

            entityManager.flush();

            FilteringDataRequest request = new FilteringDataRequest(
                    null, null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when - UTILIZE 대신 LATEST 사용
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(3);
            
            // 프로젝트 연결 수 확인
            List<DataWithProjectCountDto> content = result.getContent();
            boolean foundTwoConnections = content.stream().anyMatch(dto -> dto.countConnectedProjects() == 2);
            boolean foundOneConnection = content.stream().anyMatch(dto -> dto.countConnectedProjects() == 1);
            boolean foundZeroConnection = content.stream().anyMatch(dto -> dto.countConnectedProjects() == 0);
            
            assertThat(foundTwoConnections).isTrue();
            assertThat(foundOneConnection).isTrue();
            assertThat(foundZeroConnection).isTrue();
        }

        @Test
        @DisplayName("빈 검색 결과 처리")
        void empty_search_result_handling() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    "존재하지않는키워드", null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(0);
        }

        @Test
        @DisplayName("메타데이터와 함께 조회되는 데이터")
        void data_with_metadata_retrieval() {
            // given
            FilteringDataRequest request = new FilteringDataRequest(
                    "테스트", null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<DataWithProjectCountDto> result = searchAdapter.searchByFilters(request, pageable, DataSortType.LATEST);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            
            DataWithProjectCountDto dataDto = result.getContent().get(0);
            assertThat(dataDto.data().getTitle()).isEqualTo("테스트 데이터");
            assertThat(dataDto.countConnectedProjects()).isEqualTo(1);
        }
    }
}
