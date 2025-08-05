package com.dataracy.modules.project.application.port.in.query.extractor;

import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.domain.model.Project;

import java.util.Collection;

public interface FindProjectLabelMapUseCase {
    /**
 * 주어진 프로젝트 컬렉션에서 프로젝트 라벨 매핑 정보를 생성하여 반환합니다.
 *
 * @param savedProjects 라벨 매핑을 추출할 프로젝트 컬렉션
 * @return 프로젝트별 라벨 매핑 결과를 담은 응답 객체
 */
ProjectLabelMapResponse labelMapping(Collection<Project> savedProjects);
}
