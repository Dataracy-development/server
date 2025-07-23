package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.CountDataGroupResponse;

import java.util.List;

public interface CountDataGroupByTopicLabelUseCase {
    /**
 * 주제 레이블별로 그룹화된 데이터 그룹의 개수를 반환합니다.
 *
 * @return 주제 레이블별 데이터 그룹 개수 정보를 담은 CountDataGroupResponse 객체의 리스트
 */
List<CountDataGroupResponse> countDataGroups();
}
