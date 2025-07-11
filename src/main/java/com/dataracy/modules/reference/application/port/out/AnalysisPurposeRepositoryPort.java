package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AnalysisPurpose db에 접근하는 포트
 */
@Repository
public interface AnalysisPurposeRepositoryPort {
    List<AnalysisPurpose> allAnalysisPurposes();
    AnalysisPurpose findAnalysisPurposeById(Long analysisPurposeId);
}
