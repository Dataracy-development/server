package com.dataracy.modules.dataset.adapter.web;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.dataset.adapter.web.api.read.DataReadController;
import com.dataracy.modules.dataset.adapter.web.mapper.read.DataReadWebMapper;
import com.dataracy.modules.dataset.adapter.web.response.read.DataDetailWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.read.PopularDataWebResponse;
import com.dataracy.modules.dataset.application.dto.response.read.DataDetailResponse;
import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;
import com.dataracy.modules.dataset.application.port.in.query.read.GetDataDetailUseCase;
import com.dataracy.modules.dataset.application.port.in.query.read.GetPopularDataSetsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class DataReadControllerTest {

    @Mock private DataReadWebMapper mapper;
    @Mock private GetPopularDataSetsUseCase getPopularDataSetsUseCase;
    @Mock private GetDataDetailUseCase getDataDetailUseCase;

    @InjectMocks private DataReadController controller;

    @Test
    @DisplayName("인기 데이터셋 조회 성공 시 200 반환")
    void getPopularDataSetsShouldReturnOk() {
        // given
        PopularDataResponse resDto = new PopularDataResponse(
                1L, "데이터1", "사용자A", "경제", "통계청", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "설명입니다", "thumb.png",
                100, 2048L, 500, 20,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                5L
        );
        PopularDataWebResponse webRes = new PopularDataWebResponse(
                1L, "데이터1", "사용자A", "경제", "통계청", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "설명입니다", "thumb.png",
                100, 2048L, 500, 20,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                5L
        );

        given(getPopularDataSetsUseCase.getPopularDataSets(3)).willReturn(List.of(resDto));
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        // when
        ResponseEntity<SuccessResponse<List<PopularDataWebResponse>>> response =
                controller.getPopularDataSets(3);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).title()).isEqualTo("데이터1");
    }

    @Test
    @DisplayName("데이터 상세 조회 성공 시 200 반환")
    void getDataDetailShouldReturnOk() {
        // given
        DataDetailResponse resDto = new DataDetailResponse(
                2L, "데이터2", "사용자B",
                "profile.png", "자기소개",
                "작성자", "연구원", "의료", "보건복지부", "XLSX",
                LocalDate.of(2022, 5, 1),
                LocalDate.of(2022, 12, 31),
                "상세 설명", "가이드 문서",
                "thumb2.png", 50, 1024L,
                300, 15, "{\"preview\":true}",
                LocalDateTime.of(2022, 6, 1, 14, 30)
        );

        DataDetailWebResponse webRes = new DataDetailWebResponse(
                2L, "데이터2", "사용자B",
                "profile.png", "자기소개",
                "작성자", "연구원", "의료", "보건복지부", "XLSX",
                LocalDate.of(2022, 5, 1),
                LocalDate.of(2022, 12, 31),
                "상세 설명", "가이드 문서",
                "thumb2.png", 50, 1024L,
                300, 15, "{\"preview\":true}",
                LocalDateTime.of(2022, 6, 1, 14, 30)
        );

        given(getDataDetailUseCase.getDataDetail(2L)).willReturn(resDto);
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        // when
        ResponseEntity<SuccessResponse<DataDetailWebResponse>> response =
                controller.getDataDetail(2L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().title()).isEqualTo("데이터2");
        assertThat(response.getBody().getData().userProfileImageUrl()).isEqualTo("profile.png");
        assertThat(response.getBody().getData().analysisGuide()).isEqualTo("가이드 문서");
    }
}
