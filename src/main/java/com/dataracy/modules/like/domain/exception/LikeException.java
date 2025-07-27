package com.dataracy.modules.like.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class LikeException extends BusinessException {
    /**
     * "좋아요" 도메인에서 발생하는 비즈니스 예외를 생성합니다.
     *
     * @param errorCode 예외에 해당하는 에러 코드
     */
    public LikeException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
