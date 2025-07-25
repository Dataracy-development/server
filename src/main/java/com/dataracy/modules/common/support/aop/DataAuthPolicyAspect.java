package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.common.support.annotation.AuthorizationDataEdit;
import com.dataracy.modules.dataset.application.port.in.FindUserIdByDataIdUseCase;
import com.dataracy.modules.dataset.application.port.in.FindUserIdIncludingDeletedDataUseCase;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class DataAuthPolicyAspect {

    private final FindUserIdByDataIdUseCase findUserIdByDataIdUseCase;
    private final FindUserIdIncludingDeletedDataUseCase findUserIdIncludingDeletedDataUseCase;

    @Before("@annotation(annotation) && args(dataId,..)")
    public void checkDataEditPermission(AuthorizationDataEdit annotation, Long dataId) {
        Long authenticatedUserId = SecurityContextProvider.getAuthenticatedUserId();
        Long ownerId = annotation.restore()
                ? findUserIdIncludingDeletedDataUseCase.findUserIdIncludingDeleted(dataId)
                : findUserIdByDataIdUseCase.findUserIdByDataId(dataId);

        log.error("데이터셋 작성자만 수정 및 삭제가 가능합니다.");
        if (!ownerId.equals(authenticatedUserId)) {
            log.error("데이터셋 작성자만 {}할 수 있습니다.",
                    annotation.restore() ? "복원" : "수정 및 삭제");
            throw new DataException(DataErrorStatus.NOT_MATCH_CREATOR);
        }
    }
}
