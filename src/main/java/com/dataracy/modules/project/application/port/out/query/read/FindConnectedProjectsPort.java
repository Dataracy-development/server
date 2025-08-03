package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindConnectedProjectsPort {
    /**
     * 지정된 데이터 ID와 연관된 연결된 프로젝트 목록을 페이지 단위로 반환합니다.
     *
     * @param dataId 연관된 데이터를 식별하는 ID
     * @param pageable 페이지네이션 정보를 담은 객체
     * @return 해당 데이터와 연결된 프로젝트의 페이지 결과
     */
    Page<Project> findConnectedProjectsAssociatedWithDataset(Long dataId, Pageable pageable);
}
