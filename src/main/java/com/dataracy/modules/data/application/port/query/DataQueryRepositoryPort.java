package com.dataracy.modules.data.application.port.query;

import com.dataracy.modules.data.domain.model.Data;

import java.util.Optional;

public interface DataQueryRepositoryPort {
Optional<Data> findDataById(Long dataId);
}
