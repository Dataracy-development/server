package com.dataracy.modules.email.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class EmailException extends BusinessException {
    public EmailException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
