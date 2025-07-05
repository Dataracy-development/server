package com.dataracy.modules.user.adapter.persistence.repositoryImpl.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.AuthorLevelEntity;
import com.dataracy.modules.user.adapter.persistence.mapper.reference.AuthorLevelEntityMapper;
import com.dataracy.modules.user.adapter.persistence.repository.reference.AuthorLevelJpaRepository;
import com.dataracy.modules.user.application.port.out.reference.AuthorLevelRepositoryPort;
import com.dataracy.modules.user.domain.exception.ReferenceException;
import com.dataracy.modules.user.domain.model.reference.AuthorLevel;
import com.dataracy.modules.user.domain.status.reference.ReferenceErrorStatus;
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

    /**
     * 작성자 유형 id에 해당하는 작성자 유형이 존재하면 조회한다.
     * @param authorLevelId 작성자 유형  아이디
     * @return 작성자 유형
     */
    @Override
    public AuthorLevel findAuthorLevelById(Long authorLevelId) {
        if (authorLevelId == null) {
            return null;
        }

        AuthorLevelEntity authorLevelEntity = authorLevelJpaRepository.findById(authorLevelId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL));
        return AuthorLevelEntityMapper.toDomain(authorLevelEntity);
    }
}
