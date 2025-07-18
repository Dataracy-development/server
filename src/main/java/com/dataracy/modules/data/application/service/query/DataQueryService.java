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

    /**
     * 주어진 데이터 ID에 해당하는 데이터가 존재하는지 검증합니다.
     *
     * 데이터가 존재하지 않을 경우 {@code DataException}을 발생시키며, 오류 상태는 {@code NOT_FOUND_DATA}입니다.
     *
     * @param dataId 존재 여부를 확인할 데이터의 ID
     * @throws DataException 데이터가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateData(Long dataId) {
        boolean isExist = dataRepositoryPort.existsDataById(dataId);
        if (!isExist) {
            log.warn("데이터 ID가 존재하지 않습니다: {}", dataId);
            throw new DataException(DataErrorStatus.NOT_FOUND_DATA);
        }
    }
}
