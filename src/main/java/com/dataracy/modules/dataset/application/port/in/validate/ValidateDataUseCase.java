package com.dataracy.modules.dataset.application.port.in.validate;

public interface ValidateDataUseCase {
    /**
 * 주어진 식별자에 해당하는 데이터를 검증합니다.
 *
 * @param dataId 검증 대상 데이터의 고유 식별자
 */
    void validateData(Long dataId);
}
