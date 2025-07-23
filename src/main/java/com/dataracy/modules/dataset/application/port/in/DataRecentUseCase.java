package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;

import java.util.List;

public interface DataRecentUseCase {
/****
 * 지정된 개수만큼 최근 데이터셋 목록을 조회합니다.
 *
 * @param size 반환할 최근 데이터셋의 최대 개수
 * @return 최근 데이터셋의 최소 정보 응답 객체 리스트
 */
List<DataMinimalSearchResponse> findRecentDataSets(int size);
}
