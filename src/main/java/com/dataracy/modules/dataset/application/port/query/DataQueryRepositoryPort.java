package com.dataracy.modules.dataset.application.port.query;

import com.dataracy.modules.dataset.application.dto.response.DataWithProjectCountDto;
import com.dataracy.modules.dataset.domain.model.Data;

import java.util.List;
import java.util.Optional;

public interface DataQueryRepositoryPort {
/****
 * 주어진 식별자에 해당하는 데이터를 조회합니다.
 *
 * @param dataId 조회할 데이터의 식별자
 * @return 데이터가 존재하면 해당 Data 객체를 Optional로 감싸서 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<Data> findDataById(Long dataId);

    List<DataWithProjectCountDto> findPopularDataSets(int size);
}
