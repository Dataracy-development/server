package com.dataracy.modules.user.application.port.out.reference;

import com.dataracy.modules.user.domain.model.reference.Occupation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Occupation db에 접근하는 포트
 */
@Repository
public interface OccupationRepositoryPort {
    List<Occupation> allOccupations();
}
