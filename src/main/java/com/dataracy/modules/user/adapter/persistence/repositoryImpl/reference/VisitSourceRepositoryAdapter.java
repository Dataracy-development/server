package com.dataracy.modules.user.adapter.persistence.repositoryImpl.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.VisitSourceEntity;
import com.dataracy.modules.user.adapter.persistence.mapper.reference.VisitSourceEntityMapper;
import com.dataracy.modules.user.adapter.persistence.repository.reference.VisitSourceJpaRepository;
import com.dataracy.modules.user.application.port.out.reference.VisitSourceRepositoryPort;
import com.dataracy.modules.user.domain.exception.ReferenceException;
import com.dataracy.modules.user.domain.model.reference.VisitSource;
import com.dataracy.modules.user.domain.status.reference.ReferenceErrorStatus;
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

    @Override
    public VisitSource findVisitSourceById(Long visitSourceId) {
        VisitSourceEntity visitSourceEntity = visitSourceJpaRepository.findById(visitSourceId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE));
        return VisitSourceEntityMapper.toDomain(visitSourceEntity);
    }
}
