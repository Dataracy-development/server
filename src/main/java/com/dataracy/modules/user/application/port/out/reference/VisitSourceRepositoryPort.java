package com.dataracy.modules.user.application.port.out.reference;

import com.dataracy.modules.user.domain.model.reference.VisitSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VisitSource db에 접근하는 포트
 */
@Repository
public interface VisitSourceRepositoryPort {
    List<VisitSource> allVisitSources();
}
