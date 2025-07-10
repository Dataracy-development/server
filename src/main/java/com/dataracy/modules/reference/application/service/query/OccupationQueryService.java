package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.AllOccupationsResponse;
import com.dataracy.modules.reference.application.mapper.OccupationDtoMapper;
import com.dataracy.modules.reference.application.port.in.occupation.FindAllOccupationsUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.FindOccupationUseCase;
import com.dataracy.modules.reference.application.port.out.OccupationRepositoryPort;
import com.dataracy.modules.reference.domain.model.Occupation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OccupationQueryService implements
        FindAllOccupationsUseCase,
        FindOccupationUseCase
{
    private final OccupationDtoMapper occupationDtoMapper;
    private final OccupationRepositoryPort occupationRepositoryPort;

    /**
     * 모든 occupation 리스트를 조회한다.
     * @return occupation 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public AllOccupationsResponse allOccupations() {
        List<Occupation> occupations = occupationRepositoryPort.allOccupations();
        return occupationDtoMapper.toResponseDto(occupations);
    }

    /**
     * 경험 id로 경험을 조회한다.
     * @param occupationId 경험 id
     * @return 경험
     */
    @Override
    @Transactional(readOnly = true)
    public Occupation findOccupation(Long occupationId) {
        return occupationRepositoryPort.findOccupationById(occupationId);
    }
}
