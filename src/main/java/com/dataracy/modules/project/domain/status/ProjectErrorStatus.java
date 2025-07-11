package com.dataracy.modules.project.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorStatus implements BaseErrorCode {
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
