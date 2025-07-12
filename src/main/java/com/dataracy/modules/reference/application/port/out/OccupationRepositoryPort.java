package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.Occupation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Occupation db에 접근하는 포트
 */
public interface OccupationRepositoryPort {
    /**
 * 데이터베이스에 저장된 모든 직업 정보를 리스트로 반환합니다.
 *
 * @return 모든 직업 객체의 리스트
 */
    List<Occupation> allOccupations();

    /**
 * 주어진 ID에 해당하는 직업 정보를 Optional로 반환합니다.
 *
 * @param occupationId 조회할 직업의 고유 식별자
 * @return 해당 ID의 직업이 존재하면 Occupation을 포함한 Optional, 존재하지 않으면 빈 Optional
 */
    Optional<Occupation> findOccupationById(Long occupationId);
}
