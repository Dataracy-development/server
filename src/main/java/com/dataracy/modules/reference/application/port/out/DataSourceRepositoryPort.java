package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.DataSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DataSource db에 접근하는 포트
 */
@Repository
public interface DataSourceRepositoryPort {
    /**
     * 데이터베이스에 저장된 모든 데이터 소스 목록을 반환합니다.
     *
     * @return 모든 데이터 소스의 리스트
     */
    List<DataSource> allDataSources();

    /**
     * 주어진 ID에 해당하는 DataSource 객체를 반환합니다.
     *
     * @param dataSourceId 조회할 데이터 소스의 고유 식별자
     * @return 해당 ID의 DataSource 객체
     */
    Optional<DataSource> findDataSourceById(Long dataSourceId);
}
