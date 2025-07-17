package com.dataracy.modules.reference.adapter.persistence.impl;

import com.dataracy.modules.reference.adapter.persistence.entity.AuthorLevelEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.AuthorLevelEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.AuthorLevelJpaRepository;
import com.dataracy.modules.reference.application.port.out.AuthorLevelRepositoryPort;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthorLevelRepositoryAdapter implements AuthorLevelRepositoryPort {
    private final AuthorLevelJpaRepository authorLevelJpaRepository;

    /**
     * authorLevel 엔티티의 모든 데이터셋을 조회한다.
     * @return authorLevel 데이터셋
     */
    @Override
    public List<AuthorLevel> findAllAuthorLevels() {
        List<AuthorLevelEntity> authorLevelEntities = authorLevelJpaRepository.findAll();
        return authorLevelEntities.stream()
                .map(AuthorLevelEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 작성자 유형 ID에 해당하는 작성자 유형을 조회하여 반환한다.
     *
     * @param authorLevelId 조회할 작성자 유형의 ID
     * @return 작성자 유형이 존재하면 해당 도메인 객체를 포함하는 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<AuthorLevel> findAuthorLevelById(Long authorLevelId) {
        return authorLevelJpaRepository.findById(authorLevelId)
                .map(AuthorLevelEntityMapper::toDomain);
    }

    @Override
    public boolean existsAuthorLevelById(Long authorLevelId) {
        return authorLevelJpaRepository.existsById(authorLevelId);
    }
}
