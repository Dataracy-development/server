package com.dataracy.modules.like.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class LikeException extends BusinessException {
    public LikeException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
