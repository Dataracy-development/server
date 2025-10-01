package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.VisitSourceWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class VisitSourceWebMapperTest {
    private final VisitSourceWebMapper mapper = new VisitSourceWebMapper();

    @Test
    @DisplayName("toWebDto(single): 성공 - 필드 매핑")
    void toWebDtoSingleSuccess() {
        // given
        VisitSourceResponse src = new VisitSourceResponse(1L, "v", "l");

        // when
        VisitSourceWebResponse result = mapper.toWebDto(src);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(1L),
                () -> assertThat(result.value()).isEqualTo("v"),
                () -> assertThat(result.label()).isEqualTo("l")
        );
    }

    @Test
    @DisplayName("toWebDto(all): 성공 - 리스트 매핑, null/빈 처리")
    void toWebDtoAllSuccessAndNullsafe() {
        // given
        AllVisitSourcesResponse src = new AllVisitSourcesResponse(java.util.List.of(new VisitSourceResponse(1L,"v1","l1"), new VisitSourceResponse(2L,"v2","l2")));

        // when
        AllVisitSourcesWebResponse result = mapper.toWebDto(src);
        AllVisitSourcesWebResponse nullSafe1 = mapper.toWebDto((AllVisitSourcesResponse) null);
        AllVisitSourcesWebResponse nullSafe2 = mapper.toWebDto(new AllVisitSourcesResponse(null));

        // then
        assertAll(
                () -> assertThat(result.visitSources()).hasSize(2),
                () -> assertThat(nullSafe1.visitSources()).isEmpty(),
                () -> assertThat(nullSafe2.visitSources()).isEmpty()
        );
    }
}
