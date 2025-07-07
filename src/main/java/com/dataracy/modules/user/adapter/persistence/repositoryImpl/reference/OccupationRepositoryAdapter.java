package com.dataracy.modules.user.adapter.persistence.repositoryImpl.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.OccupationEntity;
import com.dataracy.modules.user.adapter.persistence.mapper.reference.OccupationEntityMapper;
import com.dataracy.modules.user.adapter.persistence.repository.reference.OccupationJpaRepository;
import com.dataracy.modules.user.application.port.out.reference.OccupationRepositoryPort;
import com.dataracy.modules.user.domain.exception.ReferenceException;
import com.dataracy.modules.user.domain.model.reference.Occupation;
import com.dataracy.modules.user.domain.status.reference.ReferenceErrorStatus;
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

    /**
     * 직업 id에 해당하는 직업이 존재할 경우 조회한다.
     * @param occupationId 직업 아이디
     * @return 직업
     */
    @Override
    public Occupation findOccupationById(Long occupationId) {
        if (occupationId == null) {
            return null;
        }

        OccupationEntity occupationEntity = occupationJpaRepository.findById(occupationId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_OCCUPATION));
        return OccupationEntityMapper.toDomain(occupationEntity);
    }
}
