package com.dataracy.modules.reference.adapter.persistence.repositoryImpl;

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
    public List<AuthorLevel> allAuthorLevels() {
        List<AuthorLevelEntity> authorLevelEntities = authorLevelJpaRepository.findAll();
        return authorLevelEntities.stream()
                .map(AuthorLevelEntityMapper::toDomain)
                .toList();
    }

    /**
     * 작성자 유형 id에 해당하는 작성자 유형이 존재하면 조회한다.
     * @param authorLevelId 작성자 유형  아이디
     * @return 작성자 유형
     */
    @Override
    public Optional<AuthorLevel> findAuthorLevelById(Long authorLevelId) {
        return authorLevelJpaRepository.findById(authorLevelId)
                .map(AuthorLevelEntityMapper::toDomain);
    }
}
