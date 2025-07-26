package com.dataracy.modules.comment.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class CommentException extends BusinessException {
    public CommentException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
