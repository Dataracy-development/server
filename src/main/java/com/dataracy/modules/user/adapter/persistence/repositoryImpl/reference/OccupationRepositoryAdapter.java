package com.dataracy.modules.user.adapter.persistence.repositoryImpl.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.OccupationEntity;
import com.dataracy.modules.user.adapter.persistence.mapper.reference.OccupationEntityMapper;
import com.dataracy.modules.user.adapter.persistence.repository.reference.OccupationJpaRepository;
import com.dataracy.modules.user.application.port.out.reference.OccupationRepositoryPort;
import com.dataracy.modules.user.domain.model.reference.Occupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OccupationRepositoryAdapter implements OccupationRepositoryPort {
    private final OccupationJpaRepository occupationJpaRepository;

    /**
     * occupation 엔티티의 모든 데이터셋을 조회한다.
     * @return occupation 데이터셋
     */
    @Override
    public List<Occupation> allOccupations() {
        List<OccupationEntity> occupationEntities = occupationJpaRepository.findAll();
        return occupationEntities.stream()
                .map(OccupationEntityMapper::toDomain)
                .toList();
    }
}
