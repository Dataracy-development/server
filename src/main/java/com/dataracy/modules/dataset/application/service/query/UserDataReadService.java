package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.read.DataReadDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.FindUserDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.out.query.read.FindUserDataSetsPort;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDataReadService implements FindUserDataSetsUseCase {
    private final DataReadDtoMapper dataReadDtoMapper;

    private final FindUserDataSetsPort findUserDataSetsPort;

    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;


    /**
     * 지정한 사용자가 업로드한 데이터셋 목록을 조회하고, 각 데이터셋에 대해 토픽 및 데이터 타입 라벨과 연결된 프로젝트 수를 포함한
     * UserDataResponse 페이지로 변환하여 반환한다.
     *
     * 조회는 페이지네이션(pageable)에 따라 수행되며, 내부적으로 토픽 및 데이터 타입 ID들을 모아 일괄로 라벨을 조회한 다음
     * 각 데이터셋 DTO를 매핑한다. 조회 결과가 없으면 빈 페이지를 반환한다.
     *
     * @param userId   조회할 사용자의 식별자
     * @param pageable 페이지네이션 정보
     * @return 페이지 단위의 UserDataResponse (토픽 라벨, 데이터 타입 라벨, 연결된 프로젝트 수 포함)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserDataResponse> findUserDataSets(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("FindUserDataSetsUseCase", "해당 회원이 업로드한 데이터셋 목록 조회 서비스 시작 userId=" + userId);

        Page<DataWithProjectCountDto> savedDataSets = findUserDataSetsPort.findUserDataSets(userId, pageable);

        List<Long> topicIds = savedDataSets.stream()
                .map(dto -> dto.data().getTopicId())
                .toList();
        List<Long> dataTypeIds = savedDataSets.stream()
                .map(dto -> dto.data().getDataTypeId())
                .toList();

        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
        Map<Long, String> dataTypeLabelMap = getDataTypeLabelFromIdUseCase.getLabelsByIds(dataTypeIds);

        Page<UserDataResponse> userDataResponses = savedDataSets.map(wrapper -> {
            Data data = wrapper.data();
            return dataReadDtoMapper.toResponseDto(
                    data,
                    topicLabelMap.get(data.getTopicId()),
                    dataTypeLabelMap.get(data.getDataTypeId()),
                    wrapper.countConnectedProjects()
            );
        });

        LoggerFactory.service().logSuccess("FindUserDataSetsUseCase", "해당 회원이 업로드한 데이터셋 목록 조회 서비스 종료 userId=" + userId, startTime);
        return userDataResponses;
    }
}
