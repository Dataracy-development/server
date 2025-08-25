package com.dataracy.modules.like.adapter.web.mapper;

import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LikeWebMapperTest {

    @Test
    @DisplayName("toApplicationDto_should_map_fields")
    void toApplicationDto_should_map_fields() {
        // given
        LikeWebMapper mapper = new LikeWebMapper();
        TargetLikeWebRequest web = new TargetLikeWebRequest(5L, "PROJECT", false);

        // when
        TargetLikeRequest dto = mapper.toApplicationDto(web);

        // then
        assertThat(dto.targetId()).isEqualTo(5L);
        assertThat(dto.targetType()).isEqualTo("PROJECT");
        assertThat(dto.previouslyLiked()).isFalse();
    }
}
