package com.dataracy.modules.security.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class SecurityException extends BusinessException {
    public SecurityException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
