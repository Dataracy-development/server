package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;
import com.dataracy.modules.reference.application.mapper.OccupationDtoMapper;
import com.dataracy.modules.reference.application.port.in.occupation.FindAllOccupationsUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.FindOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.out.OccupationRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.Occupation;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OccupationQueryService implements
        FindAllOccupationsUseCase,
        FindOccupationUseCase,
        ValidateOccupationUseCase,
        GetOccupationLabelFromIdUseCase
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
    public AllOccupationsResponse findAllOccupations() {
        List<Occupation> occupations = occupationRepositoryPort.findAllOccupations();
        return occupationDtoMapper.toResponseDto(occupations);
    }

    /**
     * 주어진 직업 ID에 해당하는 직업 정보를 조회하여 OccupationResponse DTO로 반환한다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 해당 직업 정보를 담은 OccupationResponse DTO
     * @throws ReferenceException 직업이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public OccupationResponse findOccupation(Long occupationId) {
        Occupation occupation = occupationRepositoryPort.findOccupationById(occupationId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_OCCUPATION));
        return occupationDtoMapper.toResponseDto(occupation);
    }

    /**
     * 주어진 직업 ID에 해당하는 직업의 존재 여부를 확인합니다.
     *
     * 직업이 존재하지 않으면 {@code ReferenceException}을 발생시킵니다.
     *
     * @param occupationId 존재 여부를 확인할 직업의 ID
     * @throws ReferenceException 직업이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateOccupation(Long occupationId) {
        Boolean isExist = occupationRepositoryPort.existsOccupationById(occupationId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_OCCUPATION);
        }
    }

    /**
     * 주어진 직업 ID에 해당하는 직업명을 반환합니다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 해당 직업의 이름(라벨)
     * @throws ReferenceException 직업이 존재하지 않을 경우 NOT_FOUND_OCCUPATION 상태로 예외가 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long occupationId) {
        Optional<String> label = occupationRepositoryPort.getLabelById(occupationId);
        if (label.isEmpty()) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_OCCUPATION);
        }
        return label.get();
    }

    /**
     * 주어진 직업 ID 목록에 대해 각 ID에 해당하는 직업명을 반환합니다.
     *
     * @param occupationIds 직업 ID의 리스트
     * @return 각 직업 ID와 해당 직업명(String)으로 이루어진 맵. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> occupationIds) {
        if (occupationIds == null || occupationIds.isEmpty()) {
            return Map.of();
        }
        return occupationRepositoryPort.getLabelsByIds(occupationIds);
    }
}
