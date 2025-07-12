package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.AuthorLevel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AuthorLevel db에 접근하는 포트
 */
@Repository
public interface AuthorLevelRepositoryPort {
    /**
     * 데이터베이스에 저장된 모든 작성자 유형(AuthorLevel) 목록을 반환합니다.
     *
     * @return 전체 작성자 유형 엔티티의 리스트
     */
    List<AuthorLevel> allAuthorLevels();

    /**
     * 주어진 ID에 해당하는 AuthorLevel 엔티티를 조회합니다.
     *
     * @param authorLevelId 조회할 AuthorLevel의 고유 식별자
     * @return 해당 ID의 AuthorLevel 객체
     */
    Optional<AuthorLevel> findAuthorLevelById(Long authorLevelId);
}
