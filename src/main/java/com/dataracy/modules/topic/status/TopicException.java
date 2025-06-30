package com.dataracy.modules.topic.status;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

public class TopicException extends BusinessException {
    public TopicException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
