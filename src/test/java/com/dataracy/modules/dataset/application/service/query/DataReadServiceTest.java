package com.dataracy.modules.dataset.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.dataracy.modules.dataset.application.dto.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.read.DataReadDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.dataset.application.port.out.query.read.*;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataReadServiceTest {

  @InjectMocks private DataReadService service;

  @Mock private DataReadDtoMapper mapper;

  @Mock private GetPopularDataSetsPort getPopularDataSetsPort;

  @Mock private GetRecentDataSetsPort getRecentDataSetsPort;

  @Mock private FindDataWithMetadataPort findDataWithMetadataPort;

  @Mock private GetDataGroupCountPort getDataGroupCountPort;

  @Mock private FindConnectedDataSetsPort findConnectedDataSetsPort;

  @Mock private GetUserInfoUseCase getUserInfoUseCase;

  @Mock private FindUsernameUseCase findUsernameUseCase;

  @Mock private FindUserThumbnailUseCase findUserThumbnailUseCase;

  @Mock private GetTopicLabelFromIdUseCase topicUseCase;

  @Mock private GetDataSourceLabelFromIdUseCase dsUseCase;

  @Mock private GetDataTypeLabelFromIdUseCase dtUseCase;

  @Mock private GetAuthorLevelLabelFromIdUseCase authorUseCase;

  @Mock private GetOccupationLabelFromIdUseCase occUseCase;

  @Mock private FindDataLabelMapUseCase labelMapUseCase;

  @Mock
  private com.dataracy.modules.dataset.application.port.out.storage.PopularDataSetsStoragePort
      popularDataSetsStoragePort;

  @Mock
  private com.dataracy.modules.dataset.application.port.in.storage
          .UpdatePopularDataSetsStorageUseCase
      updatePopularDataSetsStorageUseCase;

  private Data sample() {
    return Data.of(
        1L,
        "t",
        2L,
        3L,
        4L,
        5L,
        LocalDate.now(),
        LocalDate.now(),
        "d",
        "g",
        "f",
        "thumb",
        1,
        10L,
        DataMetadata.of(1L, 1, 1, "{}"),
        LocalDateTime.now());
  }

  @Nested
  @DisplayName("인기 데이터셋 조회")
  class PopularDataSets {

    @Test
    @DisplayName("인기 데이터셋 조회 성공 → PopularDataResponse 반환")
    void getPopularDataSetsSuccess() {
      // given
      DataWithProjectCountDto dto = new DataWithProjectCountDto(sample(), 2L);

      // 저장소에서 데이터가 없다고 Mock
      given(popularDataSetsStoragePort.getPopularDataSets()).willReturn(Optional.empty());

      given(getPopularDataSetsPort.getPopularDataSets(3)).willReturn(List.of(dto));
      given(labelMapUseCase.labelMapping(any()))
          .willReturn(
              new DataLabelMapResponse(
                  Map.of(3L, "user"),
                  Map.of(3L, "https://~~"),
                  Map.of(2L, "topic"),
                  Map.of(4L, "ds"),
                  Map.of(5L, "dt")));
      PopularDataResponse mockRes =
          new PopularDataResponse(
              1L,
              "t",
              1L,
              "u",
              "https://~~",
              "topic",
              "ds",
              "dt",
              null,
              null,
              "d",
              "thumb",
              1,
              10L,
              1,
              1,
              LocalDateTime.now(),
              2L);
      given(mapper.toResponseDto(any(), any(), any(), any(), any(), any(), any()))
          .willReturn(mockRes);

      // when
      List<PopularDataResponse> result = service.getPopularDataSets(3);

      // then
      assertAll(
          () -> assertThat(result).hasSize(1),
          () -> assertThat(result.get(0).title()).isEqualTo("t"));
    }
  }

  @Nested
  @DisplayName("데이터셋 상세 조회")
  class DataSetDetail {

    @Test
    @DisplayName("데이터셋 상세 조회 성공 → DataDetailResponse 반환")
    void getDataDetailSuccess() {
      // given
      Data d = sample();
      given(findDataWithMetadataPort.findDataWithMetadataById(1L)).willReturn(Optional.of(d));

      UserInfo info =
          new UserInfo(3L, RoleType.ROLE_USER, "e", "nick", 1L, 2L, List.of(), null, "p", "intro");
      given(getUserInfoUseCase.extractUserInfo(3L)).willReturn(info);
      given(topicUseCase.getLabelById(any())).willReturn("topic");
      given(dsUseCase.getLabelById(any())).willReturn("ds");
      given(dtUseCase.getLabelById(any())).willReturn("dt");
      given(mapper.toResponseDto(any(), any(), any(), any(), any(), any(), any(), any(), any()))
          .willReturn(
              new DataDetailResponse(
                  1L,
                  "t",
                  1L,
                  "nick",
                  "p",
                  "intro",
                  "a",
                  "o",
                  "topic",
                  "ds",
                  "dt",
                  null,
                  null,
                  "d",
                  "g",
                  "thumb",
                  1,
                  10L,
                  1,
                  1,
                  "{}",
                  LocalDateTime.now()));

      // when
      DataDetailResponse res = service.getDataDetail(1L);

      // then
      assertThat(res.creatorName()).isEqualTo("nick");
    }

    @Test
    @DisplayName("데이터셋 상세 조회 실패 → NOT_FOUND_DATA 예외 발생")
    void getDataDetailThrowsWhenNotFound() {
      // given
      given(findDataWithMetadataPort.findDataWithMetadataById(1L)).willReturn(Optional.empty());

      // when & then
      DataException ex = catchThrowableOfType(() -> service.getDataDetail(1L), DataException.class);
      assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }
  }

  @Nested
  @DisplayName("데이터셋 상세 조회")
  class RecentDataSets {

    @Test
    @DisplayName("최신 데이터셋 조회 성공 → RecentMinimalDataResponse 반환")
    void getRecentDataSetsSuccess() {
      // given
      given(getRecentDataSetsPort.getRecentDataSets(2)).willReturn(List.of(sample()));
      RecentMinimalDataResponse r =
          new RecentMinimalDataResponse(
              1L, "t", 1L, "userA", "https://~~", "thumb", LocalDateTime.now());
      given(findUsernameUseCase.findUsernamesByIds(anyList())).willReturn(Map.of(1L, "userA"));
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(anyList()))
          .willReturn(Map.of(1L, "https://~~"));
      given(mapper.toResponseDto(any(Data.class), any(), any())).willReturn(r);

      // when
      List<RecentMinimalDataResponse> list = service.getRecentDataSets(2);

      // then
      assertAll(
          () -> assertThat(list).hasSize(1),
          () -> assertThat(list.get(0).creatorName()).isEqualTo("userA"));
    }
  }

  @Nested
  @DisplayName("데이터셋 그룹 카운트")
  class DataSetGroupCount {

    @Test
    @DisplayName("데이터셋 그룹 카운트 조회 성공")
    void getDataGroupCountSuccess() {
      // given
      given(getDataGroupCountPort.getDataGroupCount())
          .willReturn(List.of(new DataGroupCountResponse(1L, "t", 2L)));

      // when
      List<DataGroupCountResponse> res = service.getDataGroupCountByTopicLabel();

      // then
      assertThat(res).hasSize(1);
    }
  }

  @Nested
  @DisplayName("연결된 데이터셋 조회")
  class ConnectedDataSets {

    @Test
    @DisplayName("프로젝트와 연결된 데이터셋 조회 성공 → ConnectedDataResponse 반환")
    void findConnectedDataSetsAssociatedWithProjectSuccess() {
      // given
      DataWithProjectCountDto dto = new DataWithProjectCountDto(sample(), 1L);
      Page<DataWithProjectCountDto> page = new PageImpl<>(List.of(dto));
      given(
              findConnectedDataSetsPort.findConnectedDataSetsAssociatedWithProject(
                  99L, PageRequest.of(0, 10)))
          .willReturn(page);
      given(labelMapUseCase.labelMapping(anyList()))
          .willReturn(new DataLabelMapResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()));
      ConnectedDataResponse res =
          new ConnectedDataResponse(
              1L,
              "t",
              1L,
              "userA",
              "https://~~",
              "topic",
              "dt",
              null,
              null,
              "thumb",
              1,
              10L,
              1,
              1,
              LocalDateTime.now(),
              1L);
      given(
              mapper.toResponseDto(
                  any(Data.class), anyString(), anyString(), anyString(), anyString(), anyLong()))
          .willReturn(res);

      // when
      Page<ConnectedDataResponse> result =
          service.findConnectedDataSetsAssociatedWithProject(99L, PageRequest.of(0, 10));

      // then
      assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("데이터셋 ID 리스트 비었을 때 → 빈 리스트 반환")
    void findDataSetsByIdsEmptyInput() {
      // when
      List<ConnectedDataResponse> res = service.findDataSetsByIds(List.of());

      // then
      assertThat(res).isEmpty();
    }
  }

  @Nested
  @DisplayName("인기 데이터셋 조회 - 캐시 관련")
  class PopularDataSetsCache {

    @Test
    @DisplayName("캐시에서 데이터를 조회할 때 - 캐시 히트")
    void getPopularDataSetsCacheHit() {
      // given
      PopularDataResponse cachedData =
          new PopularDataResponse(
              1L,
              "Test Data",
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null);
      given(popularDataSetsStoragePort.getPopularDataSets())
          .willReturn(Optional.of(List.of(cachedData)));

      // when
      List<PopularDataResponse> result = service.getPopularDataSets(5);

      // then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).id()).isEqualTo(1L);
      then(popularDataSetsStoragePort).should().getPopularDataSets();
      then(getPopularDataSetsPort).should(never()).getPopularDataSets(anyInt());
      then(updatePopularDataSetsStorageUseCase).should(never()).warmUpCacheIfNeeded(anyInt());
    }
  }
}
