package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.Occupation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Occupation db에 접근하는 포트
 */
@Repository
public interface OccupationRepositoryPort {
    List<Occupation> allOccupations();
    Occupation findOccupationById(Long occupationId);
}
