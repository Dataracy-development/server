package com.dataracy.modules.dataset.application.port.in;

public interface FindUserIdIncludingDeletedDataUseCase {
    /**
 * 삭제된 데이터를 포함하여 주어진 데이터 ID에 연결된 사용자 ID를 반환합니다.
 *
 * @param dataId 사용자 ID를 조회할 데이터의 ID
 * @return 해당 데이터에 연결된 사용자 ID
 */
Long findUserIdIncludingDeleted(Long dataId);
}
