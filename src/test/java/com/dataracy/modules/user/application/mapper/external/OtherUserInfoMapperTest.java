package com.dataracy.modules.user.application.mapper.external;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OtherUserInfoMapperTest {

    OtherUserInfoMapper mapper = new OtherUserInfoMapper();

    @Test
    @DisplayName("UserProjectResponse → GetOtherUserProjectResponse 매핑 성공")
    void toOtherUserProject_success() {
        // given
        UserProjectResponse source = new UserProjectResponse(
                1L,
                "프로젝트 제목",
                "프로젝트 내용",
                "thumb.png",
                "데이터 분석",
                "초급",
                3L,
                5L,
                100L,
                LocalDateTime.of(2023, 8, 30, 12, 0)
        );

        // when
        GetOtherUserProjectResponse result = mapper.toOtherUserProject(source);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("프로젝트 제목");
        assertThat(result.content()).isEqualTo("프로젝트 내용");
        assertThat(result.projectThumbnailUrl()).isEqualTo("thumb.png");
        assertThat(result.topicLabel()).isEqualTo("데이터 분석");
        assertThat(result.authorLevelLabel()).isEqualTo("초급");
        assertThat(result.commentCount()).isEqualTo(3L);
        assertThat(result.likeCount()).isEqualTo(5L);
        assertThat(result.viewCount()).isEqualTo(100L);
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2023, 8, 30, 12, 0));
    }

    @Test
    @DisplayName("UserDataResponse → GetOtherUserDataResponse 매핑 성공")
    void toOtherUserData_success() {
        // given
        UserDataResponse source = new UserDataResponse(
                2L,
                "데이터셋 제목",
                "주제라벨",
                "타입라벨",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "data-thumb.png",
                10,
                2048L,
                200,
                20,
                LocalDateTime.of(2023, 8, 30, 13, 0),
                7L
        );

        // when
        GetOtherUserDataResponse result = mapper.toOtherUserData(source);

        // then
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.title()).isEqualTo("데이터셋 제목");
        assertThat(result.topicLabel()).isEqualTo("주제라벨");
        assertThat(result.dataTypeLabel()).isEqualTo("타입라벨");
        assertThat(result.startDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(result.endDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(result.dataThumbnailUrl()).isEqualTo("data-thumb.png");
        assertThat(result.downloadCount()).isEqualTo(10);
        assertThat(result.sizeBytes()).isEqualTo(2048L);
        assertThat(result.rowCount()).isEqualTo(200);
        assertThat(result.columnCount()).isEqualTo(20);
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2023, 8, 30, 13, 0));
        assertThat(result.countConnectedProjects()).isEqualTo(7L);
    }
}
