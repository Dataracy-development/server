package com.dataracy.modules.dataset.application.service.validate;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.validate.ValidateDataUseCase;
import com.dataracy.modules.dataset.application.port.out.validate.CheckDataExistsByIdPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DataValidateService implements ValidateDataUseCase {
    private final CheckDataExistsByIdPort checkDataExistsByIdPort;

    /**
     * 주어진 데이터 ID에 해당하는 데이터가 존재하는지 검증합니다.
     * 데이터가 존재하지 않을 경우 DataException을 NOT_FOUND_DATA 상태로 발생시킵니다.
     *
     * @param dataId 존재 여부를 확인할 데이터의 ID
     * @throws DataException 데이터가 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateData(Long dataId) {
        Instant startTime = LoggerFactory.service().logStart("ValidateDataUseCase", "주어진 데이터 ID에 해당하는 데이터의 존재 여부를 검증 서비스 시작 dataId=" + dataId);
        boolean isExist = checkDataExistsByIdPort.existsDataById(dataId);
        if (!isExist) {
            LoggerFactory.service().logWarning("ValidateDataUseCase", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
            throw new DataException(DataErrorStatus.NOT_FOUND_DATA);
        }
        LoggerFactory.service().logSuccess("ValidateDataUseCase", "주어진 데이터 ID에 해당하는 데이터의 존재 여부를 검증 서비스 종료 dataId=" + dataId, startTime);
    }
}
