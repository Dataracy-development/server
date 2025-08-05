package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.domain.model.Data;

import java.util.Optional;

public interface FindDataPort {
    /**
 * 주어진 식별자에 해당하는 Data 엔티티를 반환합니다.
 *
 * @param dataId 조회할 Data 엔티티의 고유 식별자
 * @return 해당 식별자의 Data 엔티티가 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    Optional<Data> findDataById(Long dataId);
}
