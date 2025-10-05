package com.dataracy.modules.reference.application.service.query;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;
import com.dataracy.modules.reference.application.mapper.AuthorLevelDtoMapper;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAllAuthorLevelsUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.out.AuthorLevelPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorLevelQueryService
    implements FindAllAuthorLevelsUseCase,
        FindAuthorLevelUseCase,
        ValidateAuthorLevelUseCase,
        GetAuthorLevelLabelFromIdUseCase {
  private final AuthorLevelDtoMapper authorLevelDtoMapper;
  private final AuthorLevelPort authorLevelPort;

  // Use Case 상수 정의
  private static final String FIND_ALL_AUTHOR_LEVELS_USE_CASE = "FindAllAuthorLevelsUseCase";
  private static final String FIND_AUTHOR_LEVEL_USE_CASE = "FindAuthorLevelUseCase";
  private static final String VALIDATE_AUTHOR_LEVEL_USE_CASE = "ValidateAuthorLevelUseCase";
  private static final String GET_AUTHOR_LEVEL_LABEL_FROM_ID_USE_CASE =
      "GetAuthorLevelLabelFromIdUseCase";
  private static final String AUTHOR_LEVEL_NOT_FOUND_MESSAGE =
      "해당 작성자 유형이 존재하지 않습니다. authorLevelId=";

  /**
   * 모든 AuthorLevel 엔티티의 전체 목록을 조회하여 AllAuthorLevelsResponse DTO로 반환한다.
   *
   * @return 전체 AuthorLevel 정보를 포함하는 AllAuthorLevelsResponse 객체
   */
  @Override
  @Transactional(readOnly = true)
  public AllAuthorLevelsResponse findAllAuthorLevels() {
    Instant startTime =
        LoggerFactory.service().logStart(FIND_ALL_AUTHOR_LEVELS_USE_CASE, "모든 작성자 유형 정보 조회 서비스 시작");
    List<AuthorLevel> authorLevels = authorLevelPort.findAllAuthorLevels();
    AllAuthorLevelsResponse allAuthorLevelsResponse =
        authorLevelDtoMapper.toResponseDto(authorLevels);
    LoggerFactory.service()
        .logSuccess(FIND_ALL_AUTHOR_LEVELS_USE_CASE, "모든 작성자 유형 정보 조회 서비스 종료", startTime);
    return allAuthorLevelsResponse;
  }

  /**
   * 주어진 ID에 해당하는 작성자 등급 정보를 반환한다.
   *
   * @param authorLevelId 조회할 작성자 등급의 ID
   * @return 해당 ID의 작성자 등급 정보를 담은 AuthorLevelResponse 객체
   * @throws ReferenceException 해당 ID의 작성자 등급이 존재하지 않을 경우 발생
   */
  @Override
  @Transactional(readOnly = true)
  public AuthorLevelResponse findAuthorLevel(Long authorLevelId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_AUTHOR_LEVEL_USE_CASE,
                "주어진 ID로 작성자 유형 조회 서비스 시작 authorLevelId=" + authorLevelId);
    AuthorLevel authorLevel =
        authorLevelPort
            .findAuthorLevelById(authorLevelId)
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(
                          FIND_AUTHOR_LEVEL_USE_CASE,
                          AUTHOR_LEVEL_NOT_FOUND_MESSAGE + authorLevelId);
                  return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL);
                });
    AuthorLevelResponse authorLevelResponse = authorLevelDtoMapper.toResponseDto(authorLevel);
    LoggerFactory.service()
        .logSuccess(
            FIND_AUTHOR_LEVEL_USE_CASE,
            "주어진 ID로 작성자 유형 조회 서비스 종료 authorLevelId=" + authorLevelId,
            startTime);
    return authorLevelResponse;
  }

  /**
   * 주어진 ID로 저자 등급의 존재 여부를 확인합니다. 저자 등급이 존재하지 않을 경우 ReferenceException을 발생시킵니다.
   *
   * @param authorLevelId 확인할 저자 등급의 ID
   * @throws ReferenceException 저자 등급이 존재하지 않을 때 발생
   */
  @Override
  @Transactional(readOnly = true)
  public void validateAuthorLevel(Long authorLevelId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                VALIDATE_AUTHOR_LEVEL_USE_CASE,
                "주어진 ID에 해당하는 작성자 유형이 존재하는지 확인 서비스 시작 authorLevelId=" + authorLevelId);
    boolean isExist = authorLevelPort.existsAuthorLevelById(authorLevelId);
    if (!isExist) {
      LoggerFactory.service()
          .logWarning(
              VALIDATE_AUTHOR_LEVEL_USE_CASE, AUTHOR_LEVEL_NOT_FOUND_MESSAGE + authorLevelId);
      throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL);
    }
    LoggerFactory.service()
        .logSuccess(
            VALIDATE_AUTHOR_LEVEL_USE_CASE,
            "주어진 ID에 해당하는 작성자 유형이 존재하는지 확인 서비스 종료 authorLevelId=" + authorLevelId,
            startTime);
  }

  /**
   * 주어진 작가 등급 ID에 해당하는 라벨을 반환합니다.
   *
   * @param authorLevelId 조회할 작가 등급의 ID
   * @return 해당 작가 등급의 라벨 문자열
   * @throws ReferenceException 작가 등급이 존재하지 않을 경우 발생
   */
  @Override
  @Transactional(readOnly = true)
  public String getLabelById(Long authorLevelId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                GET_AUTHOR_LEVEL_LABEL_FROM_ID_USE_CASE,
                "주어진 작성자 유형 ID에 해당하는 라벨을 조회 서비스 시작 authorLevelId=" + authorLevelId);
    String label =
        authorLevelPort
            .getLabelById(authorLevelId)
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(
                          GET_AUTHOR_LEVEL_LABEL_FROM_ID_USE_CASE,
                          AUTHOR_LEVEL_NOT_FOUND_MESSAGE + authorLevelId);
                  return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL);
                });
    LoggerFactory.service()
        .logSuccess(
            GET_AUTHOR_LEVEL_LABEL_FROM_ID_USE_CASE,
            "주어진 작성자 유형 ID에 해당하는 라벨을 조회 서비스 종료 authorLevelId=" + authorLevelId,
            startTime);
    return label;
  }

  /**
   * 주어진 작가 레벨 ID 목록에 대해 각 ID에 해당하는 레이블을 맵 형태로 반환합니다.
   *
   * @param authorLevelIds 레이블을 조회할 작가 레벨 ID 목록
   * @return 각 작가 레벨 ID에 해당하는 레이블의 맵. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
   */
  @Override
  @Transactional(readOnly = true)
  public Map<Long, String> getLabelsByIds(List<Long> authorLevelIds) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                GET_AUTHOR_LEVEL_LABEL_FROM_ID_USE_CASE,
                "작성자 유형 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작");
    if (authorLevelIds == null || authorLevelIds.isEmpty()) {
      return Map.of();
    }
    Map<Long, String> labels = authorLevelPort.getLabelsByIds(authorLevelIds);
    LoggerFactory.service()
        .logSuccess(
            GET_AUTHOR_LEVEL_LABEL_FROM_ID_USE_CASE,
            "작성자 유형 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료",
            startTime);
    return labels;
  }
}
