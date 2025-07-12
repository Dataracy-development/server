package com.dataracy.modules.reference.adapter.persistence.repositoryImpl;

import com.dataracy.modules.reference.adapter.persistence.entity.OccupationEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.OccupationEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.OccupationJpaRepository;
import com.dataracy.modules.reference.application.port.out.OccupationRepositoryPort;
import com.dataracy.modules.reference.domain.model.Occupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * 직업 id에 해당하는 직업이 존재할 경우 조회한다.
     * @param occupationId 직업 아이디
     * @return 직업
     */
    @Override
    public Optional<Occupation> findOccupationById(Long occupationId) {
        return occupationJpaRepository.findById(occupationId)
                .map(OccupationEntityMapper::toDomain);
    }
}
