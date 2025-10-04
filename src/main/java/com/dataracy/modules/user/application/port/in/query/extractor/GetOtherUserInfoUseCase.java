package com.dataracy.modules.user.application.port.in.query.extractor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;

public interface GetOtherUserInfoUseCase {
  /**
   * 다른 사용자의 기본 정보를 조회한다.
   *
   * @param userId 조회할 다른 사용자의 고유 ID
   * @return 조회된 다른 사용자 정보를 담은 {@code GetOtherUserInfoResponse}
   */
  GetOtherUserInfoResponse getOtherUserInfo(Long userId);

  /**
   * 다른 사용자의 공개 프로젝트 목록을 페이지 단위로 조회한다.
   *
   * <p>주어진 사용자 ID의 사용자가 소유하거나 관련된 프로젝트들을 지정한 페이징 조건에 따라 반환한다.
   *
   * @param userId 조회 대상 다른 사용자의 식별자
   * @param pageable 페이지 번호·크기·정렬 등 페이징 정보
   * @return 페이징된 GetOtherUserProjectResponse 객체의 페이지
   */
  Page<GetOtherUserProjectResponse> getOtherExtraProjects(Long userId, Pageable pageable);

  /**
   * 다른 사용자의 추가 데이터셋(데이터 리소스) 목록을 페이지 단위로 조회한다.
   *
   * <p>페이지네이션 파라미터를 적용해 특정 사용자가 소유하거나 관련된 추가 데이터셋을 페이징된 형태로 반환한다.
   *
   * @param userId 조회 대상 다른 사용자의 식별자
   * @param pageable 페이지 번호·크기·정렬 정보를 포함한 페이지네이션 설정
   * @return 주어진 페이지네이션 조건에 맞는 GetOtherUserDataResponse 객체들의 Page
   */
  Page<GetOtherUserDataResponse> getOtherExtraDataSets(Long userId, Pageable pageable);
}
