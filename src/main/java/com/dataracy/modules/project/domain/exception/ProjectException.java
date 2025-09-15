package com.dataracy.modules.project.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class ProjectException extends BusinessException {
    /**
     * 프로젝트 도메인에서 발생하는 비즈니스 예외를 지정된 에러 코드와 함께 생성합니다.
     *
     * @param errorCode 예외의 원인을 나타내는 에러 코드
     */
    public ProjectException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
