package com.dataracy.modules.project.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class ProjectException extends BusinessException {
    public ProjectException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
