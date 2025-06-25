package com.dataracy.common.exception;

import com.dataracy.common.status.BaseErrorCode;
import lombok.Getter;

@Getter
public abstract class BusinessException extends CustomException {
    public BusinessException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
