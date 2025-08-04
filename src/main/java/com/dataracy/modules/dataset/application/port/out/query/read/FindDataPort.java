package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.domain.model.Data;

import java.util.Optional;

public interface FindDataPort {
    /**
     * 주어진 식별자에 해당하는 Data 엔티티를 조회합니다.
     *
     * @param dataId 조회할 Data 엔티티의 고유 식별자
     * @return Data 엔티티가 존재하면 해당 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<Data> findDataById(Long dataId);
}
