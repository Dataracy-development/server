package com.dataracy.modules.dataset.application.port.elasticsearch;

import com.dataracy.modules.dataset.application.dto.response.DataSimilarSearchResponse;
import com.dataracy.modules.dataset.domain.model.Data;

import java.util.List;

public interface DataSimilarSearchPort {
/**
 * 주어진 데이터와 유사한 데이터셋을 추천합니다.
 *
 * @param data 기준이 되는 데이터 객체
 * @param size 추천할 데이터셋의 최대 개수
 * @return 유사한 데이터셋 추천 결과 목록
 */
List<DataSimilarSearchResponse> recommendSimilarDataSets(Data data, int size);
}
