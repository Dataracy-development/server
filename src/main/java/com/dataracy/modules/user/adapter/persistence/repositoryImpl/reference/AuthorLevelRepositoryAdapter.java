package com.dataracy.modules.user.adapter.persistence.repositoryImpl.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.AuthorLevelEntity;
import com.dataracy.modules.user.adapter.persistence.mapper.reference.AuthorLevelEntityMapper;
import com.dataracy.modules.user.adapter.persistence.repository.reference.AuthorLevelJpaRepository;
import com.dataracy.modules.user.application.port.out.reference.AuthorLevelRepositoryPort;
import com.dataracy.modules.user.domain.model.reference.AuthorLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthorLevelRepositoryAdapter implements AuthorLevelRepositoryPort {
    private final AuthorLevelJpaRepository authorLevelJpaRepository;

    /**
     * authorLevel 엔티티의 모든 데이터셋을 조회한다.
     * @return authorLevel 데이터셋
     */
    @Override
    public List<AuthorLevel> allAuthorLevels() {
        List<AuthorLevelEntity> authorLevelEntities = authorLevelJpaRepository.findAll();
        return authorLevelEntities.stream()
                .map(AuthorLevelEntityMapper::toDomain)
                .toList();
    }
}
