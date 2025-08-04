package com.dataracy.modules.dataset.application.port.out.command.create;

import com.dataracy.modules.dataset.domain.model.Data;

public interface CreateDataPort {
    /**
     * 데이터 객체를 데이터베이스에 저장한 후, 저장된 인스턴스를 반환합니다.
     *
     * @param data 저장할 데이터 엔티티
     * @return 데이터베이스에 저장된 데이터 엔티티
     */
    Data saveData(Data data);
}
