package com.dataracy.modules.user.application.port.out.reference;

import com.dataracy.modules.user.domain.model.reference.AuthorLevel;
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
