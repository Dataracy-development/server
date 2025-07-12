package com.dataracy.modules.reference.adapter.persistence.repositoryImpl;

import com.dataracy.modules.reference.adapter.persistence.entity.VisitSourceEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.VisitSourceEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.VisitSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.VisitSourceRepositoryPort;
import com.dataracy.modules.reference.domain.model.VisitSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
     * 주어진 방문 경로 ID에 해당하는 방문 경로를 조회하여 Optional로 반환한다.
     *
     * @param visitSourceId 조회할 방문 경로의 ID
     * @return 방문 경로가 존재하면 해당 도메인 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<VisitSource> findVisitSourceById(Long visitSourceId) {
        return visitSourceJpaRepository.findById(visitSourceId)
                .map(VisitSourceEntityMapper::toDomain);
    }
}
