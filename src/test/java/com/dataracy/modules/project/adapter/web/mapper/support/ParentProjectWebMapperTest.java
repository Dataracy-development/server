package com.dataracy.modules.project.adapter.web.mapper.support;

import com.dataracy.modules.project.adapter.web.response.support.ParentProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ParentProjectWebMapperTest {

    private final ParentProjectWebMapper mapper = new ParentProjectWebMapper();

    @Test
    @DisplayName("성공 → ParentProjectResponse를 ParentProjectWebResponse로 매핑")
    void toWebDtoSuccess() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2025, 8, 27, 12, 0);
        ParentProjectResponse responseDto = new ParentProjectResponse(
                1L, "parent-title", "parent-content", 1L, "tester", "https://~~",
                5L, 6L, 7L, createdAt
        );

        // when
        ParentProjectWebResponse webResponse = mapper.toWebDto(responseDto);

        // then
        assertThat(webResponse.id()).isEqualTo(1L);
        assertThat(webResponse.title()).isEqualTo("parent-title");
        assertThat(webResponse.content()).isEqualTo("parent-content");
        assertThat(webResponse.creatorName()).isEqualTo("tester");
        assertThat(webResponse.commentCount()).isEqualTo(5L);
        assertThat(webResponse.likeCount()).isEqualTo(6L);
        assertThat(webResponse.viewCount()).isEqualTo(7L);
        assertThat(webResponse.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("예외 없음 → 입력이 null이면 null 반환")
    void toWebDtoNullInputReturnsNull() {
        // given
        ParentProjectResponse responseDto = null;

        // when
        ParentProjectWebResponse webResponse = mapper.toWebDto(responseDto);

        // then
        assertThat(webResponse).isNull();
    }
}
