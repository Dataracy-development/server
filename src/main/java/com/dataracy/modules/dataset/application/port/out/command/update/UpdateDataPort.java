package com.dataracy.modules.dataset.application.port.out.command.update;

import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;

public interface UpdateDataPort {
    /**
     * 지정된 데이터 ID에 해당하는 데이터 엔터티를 주어진 수정 요청 정보로 변경합니다.
     *
     * @param dataId 수정할 데이터의 고유 식별자
     * @param requestDto 데이터 수정 요청 정보를 담은 객체
     */
    void modifyData(Long dataId, ModifyDataRequest requestDto);
}
