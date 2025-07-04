package com.dataracy.modules.user.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class ReferenceException extends BusinessException {
    public ReferenceException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
