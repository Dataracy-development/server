package com.dataracy.user.status;

import com.dataracy.common.exception.BusinessException;
import com.dataracy.common.status.BaseErrorCode;

public class UserException extends BusinessException {
    public UserException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
