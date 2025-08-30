package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindUserDataSetsPort {
    /**
 * 지정한 사용자에 속한 데이터셋을 페이지 단위로 조회한다.
 *
 * 반환되는 각 항목은 프로젝트 수를 포함한 DataWithProjectCountDto이다.
 *
 * @param userId   조회할 사용자의 ID
 * @param pageable 페이지 번호·크기·정렬 정보를 담은 Pageable 객체
 * @return 사용자의 데이터셋을 담은 Page&lt;DataWithProjectCountDto&gt;
 */
Page<DataWithProjectCountDto> findUserDataSets(Long userId, Pageable pageable);
}
