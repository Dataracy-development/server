package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationDataEdit;
import com.dataracy.modules.dataset.application.port.in.query.extractor.FindUserIdIncludingDeletedUseCase;
import com.dataracy.modules.dataset.application.port.in.query.extractor.FindUserIdUseCase;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class DataAuthPolicyAspect {
    private final FindUserIdUseCase findUserIdUseCase;
    private final FindUserIdIncludingDeletedUseCase findUserIdIncludingDeletedUseCase;

    /**
     * 데이터 편집, 삭제 또는 복원 작업 전에 현재 인증된 사용자가 해당 데이터의 생성자인지 검증합니다.
     *
     * @param annotation 데이터 권한 검증을 위한 어노테이션 정보
     * @param dataId 권한 검증 대상 데이터의 식별자
     * @throws DataException 인증된 사용자가 데이터 생성자가 아닐 경우 발생하며, 편집, 삭제 또는 복원 권한이 없음을 나타냅니다.
     */
    @Before("@annotation(annotation) && args(dataId,..)")
    public void checkDataEditPermission(AuthorizationDataEdit annotation, Long dataId) {
        Long authenticatedUserId = SecurityContextProvider.getAuthenticatedUserId();
        Long ownerId = annotation.restore()
                ? findUserIdIncludingDeletedUseCase.findUserIdIncludingDeleted(dataId)
                : findUserIdUseCase.findUserIdByDataId(dataId);

        String message = annotation.restore() ? "데이터셋 작성자만 복원할 수 있습니다." : "데이터셋 작성자만 수정 및 삭제할 수 있습니다.";
        if (!ownerId.equals(authenticatedUserId)) {
            LoggerFactory.common().logWarning("Data", message);
            throw new DataException(DataErrorStatus.NOT_MATCH_CREATOR);
        }
    }
}
