package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataLabelMapServiceTest {

    @InjectMocks
    private DataLabelMapService service;

    @Mock
    private FindUsernameUseCase userCase;

    @Mock
    private FindUserThumbnailUseCase findUserThumbnailUseCase;

    @Mock
    private GetTopicLabelFromIdUseCase topicCase;

    @Mock
    private GetDataSourceLabelFromIdUseCase dsCase;

    @Mock
    private GetDataTypeLabelFromIdUseCase dtCase;

    @Test
    @DisplayName("DataWithProjectCountDto 리스트를 라벨 매핑 후 DataLabelMapResponse 반환")
    void labelMappingSuccess() {
        // given
        Data d = Data.of(
                1L,
                "t",
                2L,
                3L,
                4L,
                5L,
                null,
                null,
                "d",
                "g",
                "f",
                "t",
                1,
                1L,
                null,
                LocalDateTime.now()
        );
        DataWithProjectCountDto dto = new DataWithProjectCountDto(d, 2L);

        given(userCase.findUsernamesByIds(any()))
                .willReturn(Map.of(3L, "u"));
        given(findUserThumbnailUseCase.findUserThumbnailsByIds(any()))
                .willReturn(Map.of(3L, "http://~~"));
        given(topicCase.getLabelsByIds(any()))
                .willReturn(Map.of(2L, "topic"));
        given(dsCase.getLabelsByIds(any()))
                .willReturn(Map.of(4L, "ds"));
        given(dtCase.getLabelsByIds(any()))
                .willReturn(Map.of(5L, "dt"));

        // when
        DataLabelMapResponse res = service.labelMapping(List.of(dto));

        // then
        assertAll(
                () -> assertThat(res.usernameMap()).containsEntry(3L, "u"),
                () -> assertThat(res.userProfileUrlMap()).containsEntry(3L, "http://~~"),
                () -> assertThat(res.topicLabelMap()).containsEntry(2L, "topic"),
                () -> assertThat(res.dataSourceLabelMap()).containsEntry(4L, "ds"),
                () -> assertThat(res.dataTypeLabelMap()).containsEntry(5L, "dt")
        );
    }
}
