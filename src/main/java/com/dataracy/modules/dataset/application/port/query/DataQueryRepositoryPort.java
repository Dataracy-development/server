package com.dataracy.modules.dataset.application.port.query;

import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
import com.dataracy.modules.dataset.application.dto.response.DataWithProjectCountDto;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DataQueryRepositoryPort {
/**
 * 주어진 식별자에 해당하는 데이터를 조회합니다.
 *
 * @param dataId 조회할 데이터의 식별자
 * @return 데이터가 존재하면 해당 Data 객체를 Optional로 감싸서 반환하며, 존재하지 않으면 빈 Optional을 반환합니다.
 */
Optional<Data> findDataById(Long dataId);

/**
 * 지정된 ID에 해당하는 데이터와 그 메타데이터를 조회합니다.
 *
 * @param dataId 조회할 데이터의 고유 식별자
 * @return 데이터와 메타데이터가 존재하면 해당 Data 객체를 포함한 Optional, 없으면 빈 Optional
 */
Optional<Data> findDataWithMetadataById(Long dataId);

    /**
 * 지정된 개수만큼 인기 있는 데이터셋 목록을 조회합니다.
 *
 * @param size 반환할 데이터셋의 최대 개수
 * @return 인기 순으로 정렬된 데이터셋과 각 데이터셋의 프로젝트 수 정보를 담은 리스트
 */
List<DataWithProjectCountDto> findPopularDataSets(int size);

    Page<DataWithProjectCountDto> searchByFilters(DataFilterRequest request, Pageable pageable, DataSortType sortType);
}
