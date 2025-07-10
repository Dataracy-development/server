package com.dataracy.modules.reference.adapter.web.api.authorlevel;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.AuthorLevelWebMapper;
import com.dataracy.modules.reference.adapter.web.response.AllAuthorLevelsWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAllAuthorLevelsUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorLevelController implements AuthorLevelApi {
    private final AuthorLevelWebMapper authorLevelWebMapper;
    private final FindAllAuthorLevelsUseCase findAllAuthorLevelsUseCase;
    /**
     * 전체 작성자 유형 목록 조회
     */
    @Override
    public ResponseEntity<SuccessResponse<AllAuthorLevelsWebResponse>> allAuthorLevels (
    ) {
        AllAuthorLevelsResponse allAuthorLevelsResponse = findAllAuthorLevelsUseCase.allAuthorLevels();
        AllAuthorLevelsWebResponse allAuthorLevelsWebResponse = authorLevelWebMapper.toWebDto(allAuthorLevelsResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_AUTHOR_LEVEL_LIST, allAuthorLevelsWebResponse));
    }
}
