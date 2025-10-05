package com.dataracy.modules.project.application.port.in.query.read;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;

public interface FindUserProjectsUseCase {
  /**
   * 지정된 사용자가 소유한 프로젝트의 페이징된 목록을 조회합니다.
   *
   * <p>userId로 식별되는 사용자가 생성(또는 소유)한 프로젝트들을 Pageable로 지정된 페이지와 정렬 기준에 따라 반환합니다.
   *
   * @param userId 조회 대상 사용자 식별자
   * @param pageable 페이지 번호, 크기 및 정렬 정보를 담은 객체
   * @return 페이징된 UserProjectResponse들의 Page
   */
  Page<UserProjectResponse> findUserProjects(Long userId, Pageable pageable);

  /**
   * 사용자가 '좋아요'한 프로젝트들을 페이지 단위로 조회합니다.
   *
   * @param userId 조회 대상 사용자의 식별자
   * @param pageable 페이지 번호, 크기 및 정렬 정보를 포함하는 페이징 설정
   * @return 사용자가 좋아요한 프로젝트들을 담은 페이지(Page) 객체
   */
  Page<UserProjectResponse> findLikeProjects(Long userId, Pageable pageable);
}
