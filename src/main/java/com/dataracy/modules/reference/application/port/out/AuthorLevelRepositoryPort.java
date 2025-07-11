package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.AuthorLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AuthorLevel db에 접근하는 포트
 */
@Repository
public interface AuthorLevelRepositoryPort {
    List<AuthorLevel> allAuthorLevels();
    AuthorLevel findAuthorLevelById(Long authorLevelId);
}
