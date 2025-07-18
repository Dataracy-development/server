package com.dataracy.modules.data.application.service.query;

import com.dataracy.modules.data.application.port.in.ValidateDataUseCase;
import com.dataracy.modules.data.application.port.out.DataRepositoryPort;
import com.dataracy.modules.data.domain.exception.DataException;
import com.dataracy.modules.data.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataQueryService implements ValidateDataUseCase {

    private final DataRepositoryPort dataRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public void validateData(Long dataId) {
        boolean isExist = dataRepositoryPort.existsDataById(dataId);
        if (!isExist) {
            throw new DataException(DataErrorStatus.NOT_FOUND_DATA);
        }
    }
}
