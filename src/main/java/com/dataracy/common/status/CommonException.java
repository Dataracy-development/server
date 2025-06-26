package com.dataracy.common.status;

import com.dataracy.common.exception.CustomException;

/**
 * GlobalException은 애플리케이션 내에서 공통 규칙 위반을 나타내는
 * 커스텀 기본 클래스입니다.
 */
public class CommonException extends CustomException {
    public CommonException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
