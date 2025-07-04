package com.dataracy.modules.user.adapter.web.api.reference;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.mapper.reference.AuthorLevelWebMapper;
import com.dataracy.modules.user.adapter.web.mapper.reference.OccupationWebMapper;
import com.dataracy.modules.user.adapter.web.mapper.reference.VisitSourceWebMapper;
import com.dataracy.modules.user.adapter.web.response.reference.AllAuthorLevelsWebResponse;
import com.dataracy.modules.user.adapter.web.response.reference.AllOccupationsWebResponse;
import com.dataracy.modules.user.adapter.web.response.reference.AllVisitSourcesWebResponse;
import com.dataracy.modules.user.application.dto.response.reference.AllAuthorLevelsResponse;
import com.dataracy.modules.user.application.dto.response.reference.AllOccupationsResponse;
import com.dataracy.modules.user.application.dto.response.reference.AllVisitSourcesResponse;
import com.dataracy.modules.user.application.port.in.reference.FindAllAuthorLevelsUseCase;
import com.dataracy.modules.user.application.port.in.reference.FindAllOccupationsUseCase;
import com.dataracy.modules.user.application.port.in.reference.FindAllVisitSourcesUseCase;
import com.dataracy.modules.user.domain.status.reference.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReferenceController implements ReferenceApi {
    private final AuthorLevelWebMapper authorLevelWebMapper;
    private final OccupationWebMapper occupationWebMapper;
    private final VisitSourceWebMapper visitSourceWebMapper;

    private final FindAllAuthorLevelsUseCase findAllAuthorLevelsUseCase;
    private final FindAllOccupationsUseCase findAllOccupationsUseCase;
    private final FindAllVisitSourcesUseCase findAllVisitSourcesUseCase;

    @Override
    public ResponseEntity<SuccessResponse<AllAuthorLevelsWebResponse>> allAuthorLevels (
    ) {
        AllAuthorLevelsResponse allAuthorLevelsResponse = findAllAuthorLevelsUseCase.allAuthorLevels();
        AllAuthorLevelsWebResponse allAuthorLevelsWebResponse = authorLevelWebMapper.toWebDto(allAuthorLevelsResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_AUTHOR_LEVEL_LIST, allAuthorLevelsWebResponse));
    }

    @Override
    public ResponseEntity<SuccessResponse<AllOccupationsWebResponse>> allOccupations (
    ) {
        AllOccupationsResponse allOccupationsResponse = findAllOccupationsUseCase.allOccupations();
        AllOccupationsWebResponse allOccupationsWebResponse = occupationWebMapper.toWebDto(allOccupationsResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_OCCUPATION_LIST, allOccupationsWebResponse));
    }

    @Override
    public ResponseEntity<SuccessResponse<AllVisitSourcesWebResponse>> allVisitSources (
    ) {
        AllVisitSourcesResponse allVisitSourcesResponse = findAllVisitSourcesUseCase.allVisitSources();
        AllVisitSourcesWebResponse allVisitSourcesWebResponse = visitSourceWebMapper.toWebDto(allVisitSourcesResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_VISIT_SOURCE_LIST, allVisitSourcesWebResponse));
    }
}
