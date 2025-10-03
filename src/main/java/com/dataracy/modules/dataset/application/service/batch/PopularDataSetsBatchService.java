/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.service.batch;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.read.DataReadDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.dataset.application.port.in.storage.UpdatePopularDataSetsStorageUseCase;
import com.dataracy.modules.dataset.application.port.out.query.read.GetPopularDataSetsPort;
import com.dataracy.modules.dataset.application.port.out.storage.PopularDataSetsStoragePort;
import com.dataracy.modules.dataset.domain.model.Data;

import lombok.RequiredArgsConstructor;

/** 인기 데이터셋 목록을 주기적으로 계산하고 캐시에 저장하는 배치 서비스 */
@Service
@RequiredArgsConstructor
public class PopularDataSetsBatchService implements UpdatePopularDataSetsStorageUseCase {

  private final DataReadDtoMapper dataReadDtoMapper;

  private final FindDataLabelMapUseCase findDataLabelMapUseCase;

  private final PopularDataSetsStoragePort popularDataSetsStoragePort;
  private final GetPopularDataSetsPort getPopularDataSetsPort;

  // Service 상수 정의
  private static final String POPULAR_DATASETS_BATCH_SERVICE = "PopularDataSetsBatchService";

  /**
   * 매 5분마다 인기 데이터셋 목록을 계산하고 캐시에 저장합니다.
   *
   * <p>실제 운영에서는 더 긴 주기(예: 30분)로 설정할 수 있습니다.
   */
  @Scheduled(fixedRate = 300000) // 5분 = 300,000ms
  public void updatePopularDataSetsCache() {
    LoggerFactory.scheduler().logStart(POPULAR_DATASETS_BATCH_SERVICE);

    try {
      // Port Out을 통해 데이터베이스에서 인기 데이터셋 조회 (최대 20개)
      List<DataWithProjectCountDto> savedDataSets = getPopularDataSetsPort.getPopularDataSets(20);

      // 라벨 매핑
      DataLabelMapResponse labelResponse = findDataLabelMapUseCase.labelMapping(savedDataSets);

      // PopularDataResponse로 변환
      List<PopularDataResponse> popularDataSets =
          savedDataSets.stream()
              .map(
                  wrapper -> {
                    Data data = wrapper.data();
                    return dataReadDtoMapper.toResponseDto(
                        data,
                        labelResponse.usernameMap().get(data.getUserId()),
                        labelResponse.userProfileUrlMap().get(data.getUserId()),
                        labelResponse.topicLabelMap().get(data.getTopicId()),
                        labelResponse.dataSourceLabelMap().get(data.getDataSourceId()),
                        labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                        wrapper.countConnectedProjects());
                  })
              .toList();

      // 저장소에 저장
      popularDataSetsStoragePort.setPopularDataSets(popularDataSets);

      LoggerFactory.scheduler()
          .logComplete("PopularDataSetsBatchService - count=" + popularDataSets.size());

    } catch (Exception e) {
      LoggerFactory.scheduler().logError(POPULAR_DATASETS_BATCH_SERVICE, e);
    }
  }

  /**
   * 수동으로 인기 데이터셋 캐시를 업데이트합니다.
   *
   * @param size 조회할 데이터셋 개수
   */
  public void manualUpdatePopularDataSetsCache(int size) {
    LoggerFactory.scheduler().logStart("PopularDataSetsBatchService-Manual");

    try {
      // Port Out을 통해 데이터베이스에서 인기 데이터셋 조회
      List<DataWithProjectCountDto> savedDataSets = getPopularDataSetsPort.getPopularDataSets(size);

      // 라벨 매핑록
      DataLabelMapResponse labelResponse = findDataLabelMapUseCase.labelMapping(savedDataSets);

      // PopularDataResponse로 변환
      List<PopularDataResponse> popularDataSets =
          savedDataSets.stream()
              .map(
                  wrapper -> {
                    Data data = wrapper.data();
                    return dataReadDtoMapper.toResponseDto(
                        data,
                        labelResponse.usernameMap().get(data.getUserId()),
                        labelResponse.userProfileUrlMap().get(data.getUserId()),
                        labelResponse.topicLabelMap().get(data.getTopicId()),
                        labelResponse.dataSourceLabelMap().get(data.getDataSourceId()),
                        labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                        wrapper.countConnectedProjects());
                  })
              .toList();

      // 저장소에 저장
      popularDataSetsStoragePort.setPopularDataSets(popularDataSets);

      LoggerFactory.scheduler()
          .logComplete("PopularDataSetsBatchService-Manual - count=" + popularDataSets.size());

    } catch (Exception e) {
      LoggerFactory.scheduler().logError("PopularDataSetsBatchService-Manual", e);
    }
  }

  /**
   * 캐시가 존재하지 않을 때 즉시 업데이트합니다.
   *
   * @param size 조회할 데이터셋 개수
   */
  @Override
  public void warmUpCacheIfNeeded(int size) {
    if (!popularDataSetsStoragePort.hasValidData()) {
      LoggerFactory.scheduler().logStart("PopularDataSetsBatchService-WarmUp");
      manualUpdatePopularDataSetsCache(size);
    }
  }
}
