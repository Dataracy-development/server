package com.dataracy.modules.reference.application.port.in.authorlevel;

public interface GetAuthorLevelLabelFromIdUseCase {
    /**
 * 주어진 저자 레벨 ID에 해당하는 레이블을 반환합니다.
 *
 * @param authorLevelId 저자 레벨의 고유 식별자
 * @return 해당 저자 레벨의 레이블 문자열
 */
String getLabelById(Long authorLevelId);
}
