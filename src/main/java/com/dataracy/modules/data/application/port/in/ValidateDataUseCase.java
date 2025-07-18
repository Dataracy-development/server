package com.dataracy.modules.data.application.port.in;

public interface ValidateDataUseCase {
    /**
 * 주어진 dataId에 해당하는 데이터를 검증합니다.
 *
 * @param dataId 검증할 데이터의 고유 식별자
 */
void validateData(Long dataId);
}
