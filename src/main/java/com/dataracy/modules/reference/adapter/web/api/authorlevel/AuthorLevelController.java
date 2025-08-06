package com.dataracy.modules.reference.adapter.web.api.authorlevel;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.web.mapper.AuthorLevelWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllAuthorLevelsWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAllAuthorLevelsUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class AuthorLevelController implements AuthorLevelApi {
    private final AuthorLevelWebMapper authorLevelWebMapper;
    private final FindAllAuthorLevelsUseCase findAllAuthorLevelsUseCase;

    /**
     * 전체 작성자 레벨 목록을 조회하여 반환합니다.
     *
     * 작성자 레벨 정보를 애플리케이션 계층에서 조회한 후, 웹 응답 DTO로 변환하여 성공 응답으로 반환합니다.
     *
     * @return 전체 작성자 레벨 목록이 포함된 성공 응답의 HTTP 200 ResponseEntity
     */
    @Override
    public ResponseEntity<SuccessResponse<AllAuthorLevelsWebResponse>> findAllAuthorLevels (
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[FindAllAuthorLevels] 전체 작성자 유형 목록을 조회 API 요청 시작");
        AllAuthorLevelsWebResponse allAuthorLevelsWebResponse;

        try {
            AllAuthorLevelsResponse allAuthorLevelsResponse = findAllAuthorLevelsUseCase.findAllAuthorLevels();
            allAuthorLevelsWebResponse = authorLevelWebMapper.toWebDto(allAuthorLevelsResponse);
        } finally {
            LoggerFactory.api().logResponse("[FindAllAuthorLevels] 전체 작성자 유형 목록을 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_AUTHOR_LEVEL_LIST, allAuthorLevelsWebResponse));
    }
}
