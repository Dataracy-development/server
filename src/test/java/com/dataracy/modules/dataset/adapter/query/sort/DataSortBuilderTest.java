package com.dataracy.modules.dataset.adapter.query.sort;

import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity.dataEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DataSortBuilderTest {

    @Test
    @DisplayName("fromSortOption - null인 경우 생성일 기준 내림차순으로 정렬한다")
    void fromSortOption_WhenNull_ReturnsCreatedAtDesc() {
        // when
        OrderSpecifier<?>[] result = DataSortBuilder.fromSortOption(null, null);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result[0].getTarget()).isEqualTo(dataEntity.createdAt),
                () -> assertThat(result[0].isAscending()).isFalse()
        );
    }

    @Test
    @DisplayName("fromSortOption - LATEST인 경우 생성일 기준 내림차순으로 정렬한다")
    void fromSortOption_WhenLatest_ReturnsCreatedAtDesc() {
        // when
        OrderSpecifier<?>[] result = DataSortBuilder.fromSortOption(DataSortType.LATEST, null);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result[0].getTarget()).isEqualTo(dataEntity.createdAt),
                () -> assertThat(result[0].isAscending()).isFalse()
        );
    }

    @Test
    @DisplayName("fromSortOption - OLDEST인 경우 생성일 기준 오름차순으로 정렬한다")
    void fromSortOption_WhenOldest_ReturnsCreatedAtAsc() {
        // when
        OrderSpecifier<?>[] result = DataSortBuilder.fromSortOption(DataSortType.OLDEST, null);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result[0].getTarget()).isEqualTo(dataEntity.createdAt),
                () -> assertThat(result[0].isAscending()).isTrue()
        );
    }

    @Test
    @DisplayName("fromSortOption - DOWNLOAD인 경우 다운로드 수 기준 내림차순으로 정렬한다")
    void fromSortOption_WhenDownload_ReturnsDownloadCountDesc() {
        // when
        OrderSpecifier<?>[] result = DataSortBuilder.fromSortOption(DataSortType.DOWNLOAD, null);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result[0].getTarget()).isEqualTo(dataEntity.downloadCount),
                () -> assertThat(result[0].isAscending()).isFalse()
        );
    }

    @Test
    @DisplayName("fromSortOption - UTILIZE인 경우 프로젝트 수 기준 내림차순으로 정렬한다")
    void fromSortOption_WhenUtilize_ReturnsProjectCountDesc() {
        // given
        NumberPath<Long> projectCountPath = dataEntity.id;

        // when
        OrderSpecifier<?>[] result = DataSortBuilder.fromSortOption(DataSortType.UTILIZE, projectCountPath);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result[0].getTarget()).isEqualTo(projectCountPath),
                () -> assertThat(result[0].isAscending()).isFalse()
        );
    }
}
