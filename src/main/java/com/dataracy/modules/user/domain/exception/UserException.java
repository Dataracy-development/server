package com.dataracy.modules.user.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class UserException extends BusinessException {
    public UserException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
