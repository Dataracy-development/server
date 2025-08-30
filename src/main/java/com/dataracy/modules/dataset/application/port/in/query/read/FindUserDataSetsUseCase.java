package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindUserDataSetsUseCase {
    /**
 * 지정한 사용자의 데이터셋 목록을 페이지 형태로 조회합니다.
 *
 * 지정된 사용자 ID에 연관된 데이터셋을 Pageable에 지정된 페이지 및 정렬 조건에 따라 반환합니다.
 *
 * @param userId 조회할 사용자의 식별자
 * @param pageable 페이지 번호, 크기 및 정렬 정보를 포함하는 페이지 요청
 * @return 지정된 페이지에 해당하는 UserDataResponse 항목들을 담은 Page 객체
 */
Page<UserDataResponse> findUserDataSets(Long userId, Pageable pageable);
}
