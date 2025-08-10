package com.dataracy.modules.dataset.application.port.in.command.content;

public interface DeleteDataUseCase {
    /**
     * 주어진 데이터 ID에 해당하는 데이터를 삭제된 상태로 변경합니다.
     *
     * @param dataId 삭제 처리할 데이터의 고유 식별자
     */
    void deleteData(Long dataId);
}
