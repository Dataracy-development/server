package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.DataSource;

import java.util.List;
import java.util.Optional;

/**
 * DataSource db에 접근하는 포트
 */
public interface DataSourceRepositoryPort {
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
 * 지정된 ID를 가진 데이터 소스가 데이터베이스에 존재하는지 여부를 반환합니다.
 *
 * @param dataSourceId 데이터 소스의 고유 식별자
 * @return 데이터 소스가 존재하면 true, 그렇지 않으면 false
 */
boolean existsDataSourceById(Long dataSourceId);

    Optional<String> getLabelById(Long dataSourceId);
}
