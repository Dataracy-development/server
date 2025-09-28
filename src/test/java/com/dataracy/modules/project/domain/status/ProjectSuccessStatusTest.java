package com.dataracy.modules.project.domain.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProjectSuccessStatus 테스트")
class ProjectSuccessStatusTest {

    @Test
    @DisplayName("모든 ProjectSuccessStatus 값 확인")
    void allProjectSuccessStatuses_ShouldBeDefined() {
        // Then
        assertThat(ProjectSuccessStatus.values()).hasSize(13);
        assertThat(ProjectSuccessStatus.CREATED_PROJECT).isNotNull();
        assertThat(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS).isNotNull();
        assertThat(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS).isNotNull();
        assertThat(ProjectSuccessStatus.FIND_POPULAR_PROJECTS).isNotNull();
        assertThat(ProjectSuccessStatus.FIND_FILTERED_PROJECTS).isNotNull();
        assertThat(ProjectSuccessStatus.GET_PROJECT_DETAIL).isNotNull();
        assertThat(ProjectSuccessStatus.GET_CONTINUE_PROJECTS).isNotNull();
        assertThat(ProjectSuccessStatus.GET_CONNECTED_PROJECTS_ASSOCIATED_DATA).isNotNull();
        assertThat(ProjectSuccessStatus.MODIFY_PROJECT).isNotNull();
        assertThat(ProjectSuccessStatus.DELETE_PROJECT).isNotNull();
        assertThat(ProjectSuccessStatus.RESTORE_PROJECT).isNotNull();
        assertThat(ProjectSuccessStatus.GET_USER_PROJECTS).isNotNull();
        assertThat(ProjectSuccessStatus.GET_LIKE_PROJECTS).isNotNull();
    }

    @Test
    @DisplayName("ProjectSuccessStatus HTTP 상태 코드 확인")
    void projectSuccessStatuses_ShouldHaveCorrectHttpStatus() {
        // Then
        assertThat(ProjectSuccessStatus.CREATED_PROJECT.getHttpStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(ProjectSuccessStatus.FIND_POPULAR_PROJECTS.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(ProjectSuccessStatus.FIND_FILTERED_PROJECTS.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(ProjectSuccessStatus.GET_PROJECT_DETAIL.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(ProjectSuccessStatus.MODIFY_PROJECT.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(ProjectSuccessStatus.DELETE_PROJECT.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("ProjectSuccessStatus 코드 확인")
    void projectSuccessStatuses_ShouldHaveCorrectCode() {
        // Then
        assertThat(ProjectSuccessStatus.CREATED_PROJECT.getCode()).isEqualTo("201");
        assertThat(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS.getCode()).isEqualTo("200");
        assertThat(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS.getCode()).isEqualTo("200");
        assertThat(ProjectSuccessStatus.FIND_POPULAR_PROJECTS.getCode()).isEqualTo("200");
        assertThat(ProjectSuccessStatus.FIND_FILTERED_PROJECTS.getCode()).isEqualTo("200");
        assertThat(ProjectSuccessStatus.GET_PROJECT_DETAIL.getCode()).isEqualTo("200");
        assertThat(ProjectSuccessStatus.MODIFY_PROJECT.getCode()).isEqualTo("200");
        assertThat(ProjectSuccessStatus.DELETE_PROJECT.getCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("ProjectSuccessStatus 메시지 확인")
    void projectSuccessStatuses_ShouldHaveCorrectMessage() {
        // Then
        assertThat(ProjectSuccessStatus.CREATED_PROJECT.getMessage()).contains("제출이 완료되었습니다");
        assertThat(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS.getMessage()).contains("실시간 프로젝트 리스트를 조회하였습니다");
        assertThat(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS.getMessage()).contains("유사 프로젝트 리스트를 조회하였습니다");
        assertThat(ProjectSuccessStatus.FIND_POPULAR_PROJECTS.getMessage()).contains("인기있는 프로젝트 리스트를 조회하였습니다");
        assertThat(ProjectSuccessStatus.FIND_FILTERED_PROJECTS.getMessage()).contains("필터링된 프로젝트 리스트를 조회하였습니다");
        assertThat(ProjectSuccessStatus.GET_PROJECT_DETAIL.getMessage()).contains("프로젝트 상세 정보를 조회하였습니다");
        assertThat(ProjectSuccessStatus.MODIFY_PROJECT.getMessage()).contains("프로젝트 수정이 완료되었습니다");
        assertThat(ProjectSuccessStatus.DELETE_PROJECT.getMessage()).contains("프로젝트 삭제가 완료되었습니다");
    }
}
