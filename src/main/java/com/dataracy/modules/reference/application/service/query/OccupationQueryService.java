package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;
import com.dataracy.modules.reference.application.mapper.OccupationDtoMapper;
import com.dataracy.modules.reference.application.port.in.occupation.FindAllOccupationsUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.FindOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.out.OccupationPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.Occupation;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OccupationQueryService implements
        FindAllOccupationsUseCase,
        FindOccupationUseCase,
        ValidateOccupationUseCase,
        GetOccupationLabelFromIdUseCase
{
    private final OccupationDtoMapper occupationDtoMapper;
    private final OccupationPort occupationPort;

    /**
     * 모든 직업 정보를 조회하여 AllOccupationsResponse DTO로 반환한다.
     *
     * @return 전체 직업 목록이 포함된 AllOccupationsResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllOccupationsResponse findAllOccupations() {
        Instant startTime = LoggerFactory.service().logStart("FindAllOccupationsUseCase", "모든 직업 정보 조회 서비스 시작");
        List<Occupation> occupations = occupationPort.findAllOccupations();
        AllOccupationsResponse allOccupationsResponse = occupationDtoMapper.toResponseDto(occupations);
        LoggerFactory.service().logSuccess("FindAllOccupationsUseCase", "모든 직업 정보 조회 서비스 종료", startTime);
        return allOccupationsResponse;
    }

    /**
     * 주어진 직업 ID로 직업 정보를 조회하여 OccupationResponse DTO로 반환합니다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 해당 직업 정보를 담은 OccupationResponse DTO
     * @throws ReferenceException 직업이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public OccupationResponse findOccupation(Long occupationId) {
        Instant startTime = LoggerFactory.service().logStart("FindOccupationUseCase", "주어진 ID로 직업 조회 서비스 시작 occupationId=" + occupationId);
        Occupation occupation = occupationPort.findOccupationById(occupationId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindOccupationUseCase", "해당 직업이 존재하지 않습니다. occupationId=" + occupationId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_OCCUPATION);
                });
        OccupationResponse occupationResponse = occupationDtoMapper.toResponseDto(occupation);
        LoggerFactory.service().logSuccess("FindOccupationUseCase", "주어진 ID로 직업 조회 서비스 종료 occupationId=" + occupationId, startTime);
        return occupationResponse;
    }

    /**
     * 주어진 직업 ID에 해당하는 직업이 존재하는지 검증합니다.
     * 직업이 존재하지 않을 경우 ReferenceException을 발생시킵니다.
     *
     * @param occupationId 존재 여부를 검증할 직업의 ID
     * @throws ReferenceException 직업이 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateOccupation(Long occupationId) {
        Instant startTime = LoggerFactory.service().logStart("ValidateOccupationUseCase", "주어진 ID에 해당하는 직업이 존재하는지 확인 서비스 시작 occupationId=" + occupationId);
        Boolean isExist = occupationPort.existsOccupationById(occupationId);
        if (!isExist) {
            LoggerFactory.service().logWarning("ValidateOccupationUseCase", "해당 직업이 존재하지 않습니다. occupationId=" + occupationId);
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_OCCUPATION);
        }
        LoggerFactory.service().logSuccess("ValidateOccupationUseCase", "주어진 ID에 해당하는 직업이 존재하는지 확인 서비스 종료 occupationId=" + occupationId, startTime);
    }

    /**
     * 주어진 직업 ID에 해당하는 직업명을 반환합니다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 직업의 이름(라벨)
     * @throws ReferenceException 직업이 존재하지 않을 경우 NOT_FOUND_OCCUPATION 상태로 예외가 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long occupationId) {
        Instant startTime = LoggerFactory.service().logStart("GetOccupationLabelFromIdUseCase", "주어진 직업 ID에 해당하는 라벨을 조회 서비스 시작 occupationId=" + occupationId);
        String label = occupationPort.getLabelById(occupationId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("GetOccupationLabelFromIdUseCase", "해당 직업이 존재하지 않습니다. occupationId=" + occupationId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_OCCUPATION);
                });
        LoggerFactory.service().logSuccess("GetOccupationLabelFromIdUseCase", "주어진 직업 ID에 해당하는 라벨을 조회 서비스 종료 occupationId=" + occupationId, startTime);
        return label;
    }

    /**
     * 주어진 직업 ID 목록에 대해 각 ID에 해당하는 직업명을 맵 형태로 반환합니다.
     *
     * @param occupationIds 조회할 직업 ID 리스트
     * @return 각 직업 ID와 해당 직업명(String)으로 이루어진 맵. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> occupationIds) {
        Instant startTime = LoggerFactory.service().logStart("GetOccupationLabelFromIdUseCase", "직업 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작");
        if (occupationIds == null || occupationIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> labels = occupationPort.getLabelsByIds(occupationIds);
        LoggerFactory.service().logSuccess("GetOccupationLabelFromIdUseCase", "직업 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료", startTime);
        return labels;
    }
}
