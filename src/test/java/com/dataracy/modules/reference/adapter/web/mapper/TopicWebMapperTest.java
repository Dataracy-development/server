package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllTopicsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.TopicWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TopicWebMapperTest {
    private final TopicWebMapper mapper = new TopicWebMapper();

    @Test
    @DisplayName("toWebDto(single): 성공 - 필드 매핑")
    void toWebDtoSingleSuccess() {
        // given
        TopicResponse src = new TopicResponse(1L, "v", "l");

        // when
        TopicWebResponse result = mapper.toWebDto(src);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.value()).isEqualTo("v");
        assertThat(result.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toWebDto(all): 성공 - 리스트 매핑, null/빈 처리")
    void toWebDtoAllSuccessAndNullsafe() {
        // given
        AllTopicsResponse src = new AllTopicsResponse(java.util.List.of(new TopicResponse(1L,"v1","l1"), new TopicResponse(2L,"v2","l2")));

        // when
        AllTopicsWebResponse result = mapper.toWebDto(src);
        AllTopicsWebResponse nullSafe1 = mapper.toWebDto((AllTopicsResponse) null);
        AllTopicsWebResponse nullSafe2 = mapper.toWebDto(new AllTopicsResponse(null));

        // then
        assertThat(result.topics()).hasSize(2);
        assertThat(nullSafe1.topics()).isEmpty();
        assertThat(nullSafe2.topics()).isEmpty();
    }
}
