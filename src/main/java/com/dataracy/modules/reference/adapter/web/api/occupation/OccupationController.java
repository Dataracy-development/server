package com.dataracy.modules.reference.adapter.web.api.occupation;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.OccupationWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllOccupationsWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.port.in.occupation.FindAllOccupationsUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OccupationController implements OccupationApi {
    private final OccupationWebMapper occupationWebMapper;
    private final FindAllOccupationsUseCase findAllOccupationsUseCase;
    /****
     * 전체 직업 목록을 조회하여 성공 응답으로 반환합니다.
     *
     * @return 전체 직업 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<AllOccupationsWebResponse>> allOccupations (
    ) {
        AllOccupationsResponse allOccupationsResponse = findAllOccupationsUseCase.allOccupations();
        AllOccupationsWebResponse allOccupationsWebResponse = occupationWebMapper.toWebDto(allOccupationsResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_OCCUPATION_LIST, allOccupationsWebResponse));
    }
}
