package com.dataracy.user.status;

import com.dataracy.common.exception.BusinessException;
import com.dataracy.common.status.BaseErrorCode;

public class AuthException extends BusinessException {
    public AuthException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
