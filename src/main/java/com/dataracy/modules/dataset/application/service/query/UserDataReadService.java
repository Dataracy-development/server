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
