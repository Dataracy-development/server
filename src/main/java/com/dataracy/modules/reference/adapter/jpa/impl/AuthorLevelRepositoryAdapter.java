package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.AuthorLevelEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.AuthorLevelJpaRepository;
import com.dataracy.modules.reference.application.port.out.AuthorLevelRepositoryPort;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 주어진 ID에 해당하는 작성자 유형 도메인 객체를 Optional로 반환한다.
     *
     * @param authorLevelId 조회할 작성자 유형의 ID
     * @return 해당 ID의 작성자 유형이 존재하면 도메인 객체를 포함하는 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<AuthorLevel> findAuthorLevelById(Long authorLevelId) {
        if (authorLevelId == null) {
            return Optional.empty();
        }
        return authorLevelJpaRepository.findById(authorLevelId)
                .map(AuthorLevelEntityMapper::toDomain);
    }

    /**
     * 주어진 ID의 AuthorLevel 엔티티가 존재하는지 여부를 반환합니다.
     *
     * @param authorLevelId 확인할 AuthorLevel의 ID
     * @return 엔티티가 존재하면 true, ID가 null이거나 존재하지 않으면 false
     */
    @Override
    public boolean existsAuthorLevelById(Long authorLevelId) {
        if (authorLevelId == null) {
            return false;
        }
        return authorLevelJpaRepository.existsById(authorLevelId);
    }

    /**
     * 주어진 ID에 해당하는 AuthorLevel의 라벨을 Optional로 반환합니다.
     *
     * @param authorLevelId 조회할 AuthorLevel의 ID
     * @return 라벨 문자열을 포함하는 Optional, ID가 null이거나 엔티티가 없으면 빈 Optional 반환
     */
    @Override
    public Optional<String> getLabelById(Long authorLevelId) {
        if (authorLevelId == null) {
            return Optional.empty();
        }
        return authorLevelJpaRepository.findLabelById(authorLevelId);
    }

    /**
     * 주어진 ID 목록에 해당하는 AuthorLevel 엔티티들의 ID와 라벨을 매핑하여 반환합니다.
     *
     * @param authorLevelIds 조회할 AuthorLevel ID 목록
     * @return 각 ID에 해당하는 라벨을 담은 Map 객체
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> authorLevelIds) {
        return authorLevelJpaRepository.findAllById(authorLevelIds)
                .stream()
                .collect(Collectors.toMap(AuthorLevelEntity::getId, AuthorLevelEntity::getLabel));
    }
}
