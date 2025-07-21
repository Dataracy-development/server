package com.dataracy.modules.dataset.application.port.query;

import com.dataracy.modules.dataset.domain.model.Data;

import java.util.Optional;

public interface DataQueryRepositoryPort {
Optional<Data> findDataById(Long dataId);
}
