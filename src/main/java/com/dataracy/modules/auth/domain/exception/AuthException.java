package com.dataracy.modules.auth.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class AuthException extends BusinessException {
    public AuthException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
