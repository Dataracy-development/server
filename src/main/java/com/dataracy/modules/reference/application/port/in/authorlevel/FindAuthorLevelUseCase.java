package com.dataracy.modules.reference.application.port.in.authorlevel;

import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;

/**
 * 작성자 유형 조회 유스케이스
 */
public interface FindAuthorLevelUseCase {
    /**
     * 주어진 저자 등급 ID로 해당 저자 등급의 상세 정보를 반환합니다.
     *
     * @param authorLevelId 조회할 저자 등급의 고유 식별자
     * @return 조회된 저자 등급의 상세 정보
     */
    AuthorLevelResponse findAuthorLevel(Long authorLevelId);
}
