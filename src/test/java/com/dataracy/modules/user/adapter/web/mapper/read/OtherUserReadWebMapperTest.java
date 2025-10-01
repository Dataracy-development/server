package com.dataracy.modules.user.adapter.web.mapper.read;

import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OtherUserReadWebMapperTest {

    OtherUserReadWebMapper mapper = new OtherUserReadWebMapper();

    @Test
    @DisplayName("단일 프로젝트 DTO 매핑 성공")
    void toWebDto_project_success() {
        // given
        GetOtherUserProjectResponse dto = new GetOtherUserProjectResponse(
                1L, "프로젝트 제목", "프로젝트 내용", "thumb.png",
                "데이터 분석", "초급", 3L, 5L, 100L,
                LocalDateTime.of(2023, 8, 30, 12, 0)
        );

        // when
        GetOtherUserProjectWebResponse result = mapper.toWebDto(dto);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(1L),
                () -> assertThat(result.title()).isEqualTo("프로젝트 제목"),
                () -> assertThat(result.content()).isEqualTo("프로젝트 내용"),
                () -> assertThat(result.projectThumbnailUrl()).isEqualTo("thumb.png"),
                () -> assertThat(result.topicLabel()).isEqualTo("데이터 분석"),
                () -> assertThat(result.authorLevelLabel()).isEqualTo("초급"),
                () -> assertThat(result.commentCount()).isEqualTo(3L),
                () -> assertThat(result.likeCount()).isEqualTo(5L),
                () -> assertThat(result.viewCount()).isEqualTo(100L),
                () -> assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2023, 8, 30, 12, 0))
        );
    }

    @Test
    @DisplayName("단일 데이터 DTO 매핑 성공")
    void toWebDto_data_success() {
        // given
        GetOtherUserDataResponse dto = new GetOtherUserDataResponse(
                2L, "데이터셋 제목", "주제라벨", "타입라벨",
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31),
                "data-thumb.png", 10, 2048L, 200, 20,
                LocalDateTime.of(2023, 8, 30, 13, 0), 7L
        );

        // when
        GetOtherUserDataWebResponse result = mapper.toWebDto(dto);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(2L),
                () -> assertThat(result.title()).isEqualTo("데이터셋 제목"),
                () -> assertThat(result.topicLabel()).isEqualTo("주제라벨"),
                () -> assertThat(result.dataTypeLabel()).isEqualTo("타입라벨"),
                () -> assertThat(result.startDate()).isEqualTo(LocalDate.of(2023, 1, 1)),
                () -> assertThat(result.endDate()).isEqualTo(LocalDate.of(2023, 12, 31)),
                () -> assertThat(result.dataThumbnailUrl()).isEqualTo("data-thumb.png"),
                () -> assertThat(result.downloadCount()).isEqualTo(10),
                () -> assertThat(result.sizeBytes()).isEqualTo(2048L),
                () -> assertThat(result.rowCount()).isEqualTo(200),
                () -> assertThat(result.columnCount()).isEqualTo(20),
                () -> assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2023, 8, 30, 13, 0)),
                () -> assertThat(result.countConnectedProjects()).isEqualTo(7L)
        );
    }

    @Test
    @DisplayName("유저 정보 전체 DTO 매핑 성공 (프로젝트/데이터셋 Page 포함)")
    void toWebDto_userInfo_success() {
        // given
        GetOtherUserProjectResponse project = new GetOtherUserProjectResponse(
                3L, "Proj", "내용", "thumb.png",
                "주제", "레벨", 1L, 2L, 3L, LocalDateTime.now()
        );
        GetOtherUserDataResponse data = new GetOtherUserDataResponse(
                4L, "Data", "주제", "타입",
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "data-thumb.png", 1, 100L, 10, 5,
                LocalDateTime.now(), 2L
        );

        Page<GetOtherUserProjectResponse> projects = new PageImpl<>(List.of(project));
        Page<GetOtherUserDataResponse> datasets = new PageImpl<>(List.of(data));

        GetOtherUserInfoResponse dto = new GetOtherUserInfoResponse(
                99L, "다른유저", "중급", "데이터 분석가",
                "profile.png", "소개합니다",
                projects, datasets
        );

        // when
        GetOtherUserInfoWebResponse result = mapper.toWebDto(dto);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(99L),
                () -> assertThat(result.nickname()).isEqualTo("다른유저"),
                () -> assertThat(result.authorLevelLabel()).isEqualTo("중급"),
                () -> assertThat(result.occupationLabel()).isEqualTo("데이터 분석가"),
                () -> assertThat(result.profileImageUrl()).isEqualTo("profile.png"),
                () -> assertThat(result.introductionText()).isEqualTo("소개합니다"),
                // Page 매핑 검증
                () -> assertThat(result.projects().getContent())
                        .extracting(GetOtherUserProjectWebResponse::title)
                        .containsExactly("Proj"),
                () -> assertThat(result.datasets().getContent())
                        .extracting(GetOtherUserDataWebResponse::title)
                        .containsExactly("Data")
        );
    }
}
