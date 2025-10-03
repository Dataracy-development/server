/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.service.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataLabelMapService implements FindDataLabelMapUseCase {
  private final FindUsernameUseCase findUsernameUseCase;
  private final FindUserThumbnailUseCase findUserThumbnailUseCase;

  private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
  private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
  private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;

  /**
   * 데이터셋 DTO 컬렉션에서 사용자, 토픽, 데이터 소스, 데이터 타입의 ID를 추출하여 각 ID에 해당하는 라벨 매핑 정보를 조회합니다.
   *
   * <p>최적화: 중복 ID를 제거하여 N+1 문제를 해결하고 배치 조회로 성능을 개선합니다.
   *
   * @param savedDataSets 프로젝트 개수가 포함된 데이터셋 DTO 컬렉션
   * @return 사용자명, 사용자 프로필 이미지 URL, 토픽 라벨, 데이터 소스 라벨, 데이터 타입 라벨의 매핑 정보를 포함하는 응답 객체
   */
  @Transactional(readOnly = true)
  public DataLabelMapResponse labelMapping(Collection<DataWithProjectCountDto> savedDataSets) {
    if (savedDataSets.isEmpty()) {
      return new DataLabelMapResponse(
          Collections.emptyMap(),
          Collections.emptyMap(),
          Collections.emptyMap(),
          Collections.emptyMap(),
          Collections.emptyMap());
    }

    // 중복 제거된 ID 목록 생성 (N+1 문제 해결)
    Set<Long> uniqueUserIds =
        savedDataSets.stream().map(dto -> dto.data().getUserId()).collect(Collectors.toSet());
    Set<Long> uniqueTopicIds =
        savedDataSets.stream().map(dto -> dto.data().getTopicId()).collect(Collectors.toSet());
    Set<Long> uniqueDataSourceIds =
        savedDataSets.stream().map(dto -> dto.data().getDataSourceId()).collect(Collectors.toSet());
    Set<Long> uniqueDataTypeIds =
        savedDataSets.stream().map(dto -> dto.data().getDataTypeId()).collect(Collectors.toSet());

    // Set을 List로 변환하여 배치 조회
    return new DataLabelMapResponse(
        findUsernameUseCase.findUsernamesByIds(new ArrayList<>(uniqueUserIds)),
        findUserThumbnailUseCase.findUserThumbnailsByIds(new ArrayList<>(uniqueUserIds)),
        getTopicLabelFromIdUseCase.getLabelsByIds(new ArrayList<>(uniqueTopicIds)),
        getDataSourceLabelFromIdUseCase.getLabelsByIds(new ArrayList<>(uniqueDataSourceIds)),
        getDataTypeLabelFromIdUseCase.getLabelsByIds(new ArrayList<>(uniqueDataTypeIds)));
  }
}
