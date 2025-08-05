package com.dataracy.modules.dataset.application.port.out.command.create;

import com.dataracy.modules.dataset.domain.model.Data;

public interface CreateDataPort {
    /**
 * 주어진 데이터 엔티티를 영속화하고, 저장된 결과 인스턴스를 반환합니다.
 *
 * @param data 저장할 데이터 엔티티
 * @return 저장된 데이터 엔티티 인스턴스
 */
    Data saveData(Data data);
}
