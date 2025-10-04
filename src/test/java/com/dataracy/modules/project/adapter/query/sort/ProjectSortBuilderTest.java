package com.dataracy.modules.project.adapter.query.sort;

import static com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity.projectEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.domain.enums.ProjectSortType;

import com.querydsl.core.types.OrderSpecifier;

class ProjectSortBuilderTest {

  @Test
  @DisplayName("fromSortOption - null인 경우 생성일 기준 내림차순으로 정렬한다")
  void fromSortOptionWhenNullReturnsCreatedAtDesc() {
    // when
    OrderSpecifier<?>[] result = ProjectSortBuilder.fromSortOption(null);

    // then
    assertAll(
        () -> assertThat(result).hasSize(1),
        () -> assertThat(result[0].getTarget()).isEqualTo(projectEntity.createdAt),
        () -> assertThat(result[0].isAscending()).isFalse());
  }

  @Test
  @DisplayName("fromSortOption - LATEST인 경우 생성일 기준 내림차순으로 정렬한다")
  void fromSortOptionWhenLatestReturnsCreatedAtDesc() {
    // when
    OrderSpecifier<?>[] result = ProjectSortBuilder.fromSortOption(ProjectSortType.LATEST);

    // then
    assertAll(
        () -> assertThat(result).hasSize(1),
        () -> assertThat(result[0].getTarget()).isEqualTo(projectEntity.createdAt),
        () -> assertThat(result[0].isAscending()).isFalse());
  }

  @Test
  @DisplayName("fromSortOption - OLDEST인 경우 생성일 기준 오름차순으로 정렬한다")
  void fromSortOptionWhenOldestReturnsCreatedAtAsc() {
    // when
    OrderSpecifier<?>[] result = ProjectSortBuilder.fromSortOption(ProjectSortType.OLDEST);

    // then
    assertAll(
        () -> assertThat(result).hasSize(1),
        () -> assertThat(result[0].getTarget()).isEqualTo(projectEntity.createdAt),
        () -> assertThat(result[0].isAscending()).isTrue());
  }

  @Test
  @DisplayName("fromSortOption - MOST_LIKED인 경우 좋아요 수 기준 내림차순으로 정렬한다")
  void fromSortOptionWhenMostLikedReturnsLikeCountDesc() {
    // when
    OrderSpecifier<?>[] result = ProjectSortBuilder.fromSortOption(ProjectSortType.MOST_LIKED);

    // then
    assertAll(
        () -> assertThat(result).hasSize(1),
        () -> assertThat(result[0].getTarget()).isEqualTo(projectEntity.likeCount),
        () -> assertThat(result[0].isAscending()).isFalse());
  }

  @Test
  @DisplayName("fromSortOption - MOST_VIEWED인 경우 조회 수 기준 내림차순으로 정렬한다")
  void fromSortOptionWhenMostViewedReturnsViewCountDesc() {
    // when
    OrderSpecifier<?>[] result = ProjectSortBuilder.fromSortOption(ProjectSortType.MOST_VIEWED);

    // then
    assertAll(
        () -> assertThat(result).hasSize(1),
        () -> assertThat(result[0].getTarget()).isEqualTo(projectEntity.viewCount),
        () -> assertThat(result[0].isAscending()).isFalse());
  }

  @Test
  @DisplayName("fromSortOption - MOST_COMMENTED인 경우 댓글 수 기준 내림차순으로 정렬한다")
  void fromSortOptionWhenMostCommentedReturnsCommentCountDesc() {
    // when
    OrderSpecifier<?>[] result = ProjectSortBuilder.fromSortOption(ProjectSortType.MOST_COMMENTED);

    // then
    assertAll(
        () -> assertThat(result).hasSize(1),
        () -> assertThat(result[0].getTarget()).isEqualTo(projectEntity.commentCount),
        () -> assertThat(result[0].isAscending()).isFalse());
  }

  @Test
  @DisplayName("fromSortOption - LEAST_COMMENTED인 경우 댓글 수 기준 오름차순으로 정렬한다")
  void fromSortOptionWhenLeastCommentedReturnsCommentCountAsc() {
    // when
    OrderSpecifier<?>[] result = ProjectSortBuilder.fromSortOption(ProjectSortType.LEAST_COMMENTED);

    // then
    assertAll(
        () -> assertThat(result).hasSize(1),
        () -> assertThat(result[0].getTarget()).isEqualTo(projectEntity.commentCount),
        () -> assertThat(result[0].isAscending()).isTrue());
  }
}
