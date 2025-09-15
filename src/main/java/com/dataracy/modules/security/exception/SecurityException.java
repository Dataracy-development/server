package com.dataracy.modules.security.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class SecurityException extends BusinessException {
    /**
     * 주어진 에러 코드를 사용하여 인증/인가 도메인 관련 비즈니스 예외를 생성합니다.
     *
     * @param errorCode 예외의 원인을 나타내는 에러 코드
     */
    public SecurityException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
