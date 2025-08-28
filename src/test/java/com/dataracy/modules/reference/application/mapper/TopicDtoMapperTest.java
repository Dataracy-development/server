package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import com.dataracy.modules.reference.domain.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TopicDtoMapperTest {
    private final TopicDtoMapper mapper = new TopicDtoMapper();

    @Test
    @DisplayName("toResponseDto(single): 성공 - 도메인에서 DTO로 매핑")
    void toResponseDtoSingleSuccess() {
        // given
        Topic domain = new Topic(1L, "v", "l");

        // when
        TopicResponse dto = mapper.toResponseDto(domain);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.value()).isEqualTo("v");
        assertThat(dto.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toResponseDto(list): 성공 - 리스트 변환")
    void toResponseDtoListSuccess() {
        // given
        List<Topic> domains = List.of(new Topic(1L,"v1","l1"), new Topic(2L,"v2","l2"));

        // when
        AllTopicsResponse all = mapper.toResponseDto(domains);

        // then
        assertThat(all.topics()).hasSize(2);
        assertThat(all.topics().get(0).id()).isEqualTo(1L);
    }
}
