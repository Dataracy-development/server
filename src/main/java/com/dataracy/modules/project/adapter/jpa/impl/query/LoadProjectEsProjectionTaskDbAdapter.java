package com.dataracy.modules.project.adapter.jpa.impl.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import com.dataracy.modules.project.application.port.out.query.projection.LoadProjectProjectionTaskPort;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoadProjectEsProjectionTaskDbAdapter implements LoadProjectProjectionTaskPort {
    private final ProjectEsProjectionTaskRepository repo;

    /**
     * 지정된 시점과 상태로 작업 대상인 프로젝션 태스크의 배치 목록을 조회한다.
     *
     * @param now 조회 기준 시각(해당 시각 이전/이후 조건으로 처리 대상 판단에 사용)
     * @param statuses 조회할 태스크 상태들의 목록(이 상태들에 해당하는 태스크만 반환)
     * @param pageable 페이징 및 정렬 정보
     * @return 조회된 ProjectEsProjectionTaskEntity의 리스트
     */
    @Override
    public List<ProjectEsProjectionTaskEntity> findBatchForWork(
            LocalDateTime now,
            List<ProjectEsProjectionType> statuses,
            Pageable pageable
    ) {
        return repo.findBatchForWork(
                now,
                statuses,
                pageable
        );
    }
}
