package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindUserProjectsPort {
    /**
 * 지정한 사용자가 소유하거나 관련된 프로젝트들을 페이지 단위로 조회합니다.
 *
 * @param userId   조회할 사용자의 고유 식별자
 * @param pageable 페이지 번호와 크기, 정렬 정보를 포함한 페이징 설정
 * @return 사용자와 연관된 Project 객체들의 페이지(Page)
 */
Page<Project> findUserProjects(Long userId, Pageable pageable);
    /**
 * 특정 사용자가 관련된(좋아요 기반 또는 부분 일치 등 연관성 기준) 프로젝트를 페이징하여 조회합니다.
 *
 * @param userId   조회 대상 사용자의 식별자
 * @param pageable 페이징 및 정렬 정보
 * @return 주어진 페이지 설정에 따른 Project 객체의 Page
 */
Page<Project> findLikeProjects(Long userId, Pageable pageable);
}
