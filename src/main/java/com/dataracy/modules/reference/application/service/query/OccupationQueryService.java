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
     * 모든 직업(occupation) 목록을 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 직업 목록이 포함된 AllOccupationsResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllOccupationsResponse allOccupations() {
        List<Occupation> occupations = occupationRepositoryPort.allOccupations();
        return occupationDtoMapper.toResponseDto(occupations);
    }

    /**
     * 주어진 직업 ID로 직업 정보를 조회하여 응답 DTO로 반환한다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 해당 ID에 해당하는 직업의 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public AllOccupationsResponse.OccupationResponse findOccupation(Long occupationId) {
        Occupation occupation = occupationRepositoryPort.findOccupationById(occupationId);
        return occupationDtoMapper.toResponseDto(occupation);
    }
}
