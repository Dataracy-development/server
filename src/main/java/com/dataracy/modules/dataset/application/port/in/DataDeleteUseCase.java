package com.dataracy.modules.dataset.application.port.in;

public interface DataDeleteUseCase {
    /**
 * 지정된 데이터 ID에 해당하는 데이터를 삭제 상태로 표시합니다.
 *
 * @param dataId 삭제 상태로 표시할 데이터의 고유 식별자
 */
void markAsDelete(Long dataId);
}
