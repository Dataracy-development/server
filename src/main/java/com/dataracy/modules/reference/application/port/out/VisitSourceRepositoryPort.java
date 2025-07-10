package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.VisitSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VisitSource db에 접근하는 포트
 */
@Repository
public interface VisitSourceRepositoryPort {
    List<VisitSource> allVisitSources();
    VisitSource findVisitSourceById(Long visitSourceId);
}
