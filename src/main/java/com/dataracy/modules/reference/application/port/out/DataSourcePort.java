package com.dataracy.modules.reference.application.port.out;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dataracy.modules.reference.domain.model.DataSource;

public interface DataSourcePort {
  /**
   * 데이터베이스에 저장된 모든 데이터 소스의 리스트를 반환합니다.
   *
   * @return 데이터 소스 객체들의 리스트
   */
  List<DataSource> findAllDataSources();

  /**
   * 데이터 소스의 고유 ID로 DataSource 객체를 조회합니다.
   *
   * @param dataSourceId 조회할 데이터 소스의 고유 식별자
   * @return 해당 ID의 DataSource가 존재하면 Optional로 반환하며, 존재하지 않으면 빈 Optional을 반환합니다.
   */
  Optional<DataSource> findDataSourceById(Long dataSourceId);

  /**
   * 지정된 ID의 데이터 소스가 데이터베이스에 존재하는지 확인합니다.
   *
   * @param dataSourceId 데이터 소스의 고유 식별자
   * @return 데이터 소스가 존재하면 true, 존재하지 않으면 false
   */
  boolean existsDataSourceById(Long dataSourceId);

  /**
   * 지정된 ID에 해당하는 데이터 소스의 라벨을 반환합니다.
   *
   * @param dataSourceId 라벨을 조회할 데이터 소스의 고유 ID
   * @return 라벨이 존재하면 해당 문자열을 포함한 Optional, 존재하지 않으면 빈 Optional
   */
  Optional<String> getLabelById(Long dataSourceId);

  /**
   * 주어진 데이터 소스 ID 목록에 대해 각 ID와 해당 데이터 소스의 라벨을 매핑한 Map을 반환합니다.
   *
   * @param dataSourceIds 라벨을 조회할 데이터 소스의 ID 목록
   * @return 각 데이터 소스 ID와 해당 라벨이 매핑된 Map
   */
  Map<Long, String> getLabelsByIds(List<Long> dataSourceIds);
}
