package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllAuthorLevelsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.AuthorLevelWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorLevelWebMapperTest {
    private final AuthorLevelWebMapper mapper = new AuthorLevelWebMapper();

    @Test
    @DisplayName("toWebDto(single): 성공 - 필드 매핑")
    void toWebDtoSingleSuccess() {
        // given
        AuthorLevelResponse src = new AuthorLevelResponse(1L, "v", "l");

        // when
        AuthorLevelWebResponse result = mapper.toWebDto(src);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.value()).isEqualTo("v");
        assertThat(result.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toWebDto(all): 성공 - 리스트 매핑, null/빈 처리")
    void toWebDtoAllSuccessAndNullsafe() {
        // given
        AllAuthorLevelsResponse src = new AllAuthorLevelsResponse(java.util.List.of(new AuthorLevelResponse(1L,"v1","l1"), new AuthorLevelResponse(2L,"v2","l2")));

        // when
        AllAuthorLevelsWebResponse result = mapper.toWebDto(src);
        AllAuthorLevelsWebResponse nullSafe1 = mapper.toWebDto((AllAuthorLevelsResponse) null);
        AllAuthorLevelsWebResponse nullSafe2 = mapper.toWebDto(new AllAuthorLevelsResponse(null));

        // then
        assertThat(result.authorLevels()).hasSize(2);
        assertThat(nullSafe1.authorLevels()).isEmpty();
        assertThat(nullSafe2.authorLevels()).isEmpty();
    }
}
