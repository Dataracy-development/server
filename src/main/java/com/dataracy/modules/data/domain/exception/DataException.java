package com.dataracy.modules.data.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class DataException extends BusinessException {
    /**
     * 데이터 도메인에서 발생하는 비즈니스 예외를 지정된 에러 코드와 함께 생성합니다.
     *
     * @param errorCode 예외의 원인을 나타내는 에러 코드
     */
    public DataException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
