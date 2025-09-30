package com.dataracy.modules.comment.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

/**
 * 댓글 도메인 관련 비즈니스 예외
 * 
 * 상속 구조: Object → Throwable → Exception → RuntimeException → CustomException → BusinessException → CommentException
 * 
 * BusinessException을 통해 비즈니스 예외와 일반 예외(CommonException)를 명확히 구분하며,
 * 9개의 도메인 예외(Auth, User, Project, Data, Comment, Like, Email, Reference, Security)가
 * 동일한 계층 구조를 공유하여 일관된 예외 처리를 가능하게 합니다.
 */
@SuppressWarnings("squid:S110") // 상속 깊이는 의도된 아키텍처 설계
public class CommentException extends BusinessException {
    /**
     * 주어진 에러 코드를 사용하여 댓글 도메인 관련 비즈니스 예외를 생성합니다.
     *
     * @param errorCode 예외의 원인을 나타내는 에러 코드
     */
    public CommentException(BaseErrorCode errorCode){
        super(errorCode);
    }
}
