package com.dataracy.common.exception;

import com.dataracy.common.status.BaseErrorCode;
import lombok.Getter;

/**
 * BusinessException은 애플리케이션 내에서 도메인, 비즈니스 규칙 위반을 나타내는
 * 모든 커스텀 예외의 기본 클래스입니다.
 *
 * 이 클래스를 상속하는 하위 클래스는, 문제의 원인과 해결 방법을
 * 클라이언트와 개발자가 명확히 이해할 수 있도록 도와주는
 * **구체적인 에러 코드**를 제공해야 합니다.
 */
@Getter
public abstract class BusinessException extends CustomException {
    public BusinessException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
