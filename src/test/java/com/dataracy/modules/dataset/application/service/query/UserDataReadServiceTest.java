package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.read.DataReadDtoMapper;
import com.dataracy.modules.dataset.application.port.out.query.read.FindUserDataSetsPort;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDataReadServiceTest {
    @InjectMocks
    private UserDataReadService service;

    @Mock
    private DataReadDtoMapper mapper;

    @Mock
    private FindUserDataSetsPort findUserDataSetsPort;

    @Mock
    private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

    @Mock
    private GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;

    private Data sample() {
        return Data.of(
                1L,
                "t",
                2L,
                3L,
                4L,
                5L,
                LocalDate.now(),
                LocalDate.now(),
                "d",
                "g",
                "f",
                "thumb",
                1,
                10L,
                DataMetadata.of(1L, 1, 1, "{}"),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("조회 결과가 없을 경우 빈 Page 반환")
    void getUserDataSetsEmpty() {
        // given
        given(findUserDataSetsPort.findUserDataSets(eq(1L), any()))
                .willReturn(Page.empty());

        // when
        Page<UserDataResponse> result = service.findUserDataSets(1L, PageRequest.of(0, 10));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조회된 데이터셋마다 매퍼가 호출되고 라벨이 올바르게 매핑된다")
    void getUserDataSetsWithLabels() {
        // given
        Data data1 = sample();
        Data data2 = Data.of(2L, "title2", 20L, 30L, 40L, 50L,
                LocalDate.now(), LocalDate.now(), "d2", "g2", "f2", "thumb2", 1, 20L,
                DataMetadata.of(2L, 2, 2, "{}"), LocalDateTime.now());

        Page<DataWithProjectCountDto> page = new PageImpl<>(List.of(
                new DataWithProjectCountDto(data1, 2L),
                new DataWithProjectCountDto(data2, 3L)
        ));
        given(findUserDataSetsPort.findUserDataSets(eq(1L), any())).willReturn(page);

        given(getTopicLabelFromIdUseCase.getLabelsByIds(any()))
                .willReturn(Map.of(data1.getTopicId(), "토픽1", data2.getTopicId(), "토픽2"));
        given(getDataTypeLabelFromIdUseCase.getLabelsByIds(any()))
                .willReturn(Map.of(data1.getDataTypeId(), "타입1", data2.getDataTypeId(), "타입2"));

        given(mapper.toResponseDto(any(), any(), any(), any()))
                .willAnswer(inv -> {
                    Data d = inv.getArgument(0);
                    String topic = inv.getArgument(1);
                    String type = inv.getArgument(2);
                    Long count = inv.getArgument(3);
                    return new UserDataResponse(
                            d.getId(), d.getTitle(), topic, type,
                            null, null, d.getDataThumbnailUrl(), 1, d.getUserId(), 1, 1,
                            LocalDateTime.now(), count
                    );
                });

        // when
        Page<UserDataResponse> result = service.findUserDataSets(1L, PageRequest.of(0, 10));

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.getContent().get(0).topicLabel()).isEqualTo("토픽1"),
                () -> assertThat(result.getContent().get(1).dataTypeLabel()).isEqualTo("타입2")
        );
    }
}
