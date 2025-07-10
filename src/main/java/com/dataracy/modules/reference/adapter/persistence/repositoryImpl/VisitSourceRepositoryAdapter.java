package com.dataracy.modules.reference.adapter.persistence.repositoryImpl;

import com.dataracy.modules.reference.adapter.persistence.entity.VisitSourceEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.VisitSourceEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.VisitSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.VisitSourceRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.VisitSource;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VisitSourceRepositoryAdapter implements VisitSourceRepositoryPort {
    private final VisitSourceJpaRepository visitSourceJpaRepository;

    /**
     * visitSource 엔티티의 모든 데이터셋을 조회한다.
     * @return visitSource 데이터셋
     */
    @Override
    public List<VisitSource> allVisitSources() {
        List<VisitSourceEntity> visitSourceEntities = visitSourceJpaRepository.findAll();
        return visitSourceEntities.stream()
                .map(VisitSourceEntityMapper::toDomain)
                .toList();
    }

    /**
     * 방문 경로 id에 해당하는 방문 경로가 존재하면 방문 경로를 조회한다.
     * @param visitSourceId 방문 경로 id
     * @return 방문 경로
     */
    @Override
    public VisitSource findVisitSourceById(Long visitSourceId) {
        if (visitSourceId == null) {
            return null;
        }

        VisitSourceEntity visitSourceEntity = visitSourceJpaRepository.findById(visitSourceId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE));
        return VisitSourceEntityMapper.toDomain(visitSourceEntity);
    }
}
