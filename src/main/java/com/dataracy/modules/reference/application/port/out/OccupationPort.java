package com.dataracy.modules.reference.application.port.out;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dataracy.modules.reference.domain.model.Occupation;

public interface OccupationPort {
  /**
   * 데이터베이스에 저장된 모든 직업 정보를 리스트로 반환합니다.
   *
   * @return 모든 직업 객체의 리스트
   */
  List<Occupation> findAllOccupations();

  /**
   * 주어진 ID에 해당하는 직업 정보를 조회하여 Optional로 반환합니다.
   *
   * @param occupationId 조회할 직업의 고유 식별자
   * @return 해당 ID의 직업이 존재하면 Occupation을 포함한 Optional, 존재하지 않으면 빈 Optional
   */
  Optional<Occupation> findOccupationById(Long occupationId);

  /**
   * 주어진 ID에 해당하는 직업이 데이터베이스에 존재하는지 확인합니다.
   *
   * @param occupationId 존재 여부를 확인할 직업의 고유 식별자
   * @return 직업이 존재하면 true, 존재하지 않으면 false
   */
  boolean existsOccupationById(Long occupationId);

  /**
   * 주어진 직업 ID에 해당하는 직업의 라벨(이름 또는 설명)을 반환합니다.
   *
   * @param occupationId 라벨을 조회할 직업의 고유 ID
   * @return 직업이 존재하면 해당 라벨을 포함한 Optional, 존재하지 않으면 빈 Optional
   */
  Optional<String> getLabelById(Long occupationId);

  /**
   * 여러 직업 ID에 대해 각 ID와 해당 직업 라벨을 매핑한 맵을 반환합니다.
   *
   * @param occupationIds 라벨을 조회할 직업 ID 목록
   * @return 각 직업 ID와 해당 라벨이 매핑된 맵
   */
  Map<Long, String> getLabelsByIds(List<Long> occupationIds);
}
