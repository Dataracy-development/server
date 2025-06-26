package com.dataracy.common.status;

import com.dataracy.common.exception.BusinessException;

public class GlobalException extends BusinessException {
    public GlobalException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
