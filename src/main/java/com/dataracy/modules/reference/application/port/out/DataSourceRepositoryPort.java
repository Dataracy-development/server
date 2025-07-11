package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.DataSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DataSource db에 접근하는 포트
 */
@Repository
public interface DataSourceRepositoryPort {
    List<DataSource> allDataSources();
    DataSource findDataSourceById(Long dataSourceId);
}
