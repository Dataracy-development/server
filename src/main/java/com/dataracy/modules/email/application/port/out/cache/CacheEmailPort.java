package com.dataracy.modules.email.application.port.out.cache;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;

public interface CacheEmailPort {
    /**
     * 이메일과 인증 유형에 해당하는 인증 코드를 저장합니다.
     *
     * @param email 인증 코드를 저장할 이메일 주소
     * @param code 저장할 인증 코드
     * @param verificationType 이메일 인증 유형
     */
    void saveCode(String email, String code, EmailVerificationType verificationType);

    /**
     * 저장된 이메일 인증 코드와 입력된 코드를 비교하여 인증을 검증합니다.
     *
     * @param email 인증을 시도하는 이메일 주소
     * @param code 사용자가 입력한 인증 코드
     * @param verificationType 인증 코드의 유형
     * @return 인증 결과에 대한 문자열 값
     */
    String verifyCode(String email, String code, EmailVerificationType verificationType);

    /**
     * 지정된 이메일과 인증 유형에 해당하는 인증 코드를 캐시에서 삭제합니다.
     *
     * @param email 인증 코드를 삭제할 이메일 주소
     * @param verificationType 삭제할 인증 코드의 유형
     */
    void deleteCode(String email, EmailVerificationType verificationType);
}
