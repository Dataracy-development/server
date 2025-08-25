package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataSourceWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataSourceWebMapperTest {
    private final DataSourceWebMapper mapper = new DataSourceWebMapper();

    @Test
    @DisplayName("toWebDto(single): 성공 - 필드 매핑")
    void toWebDto_single_success() {
        // given
        DataSourceResponse src = new DataSourceResponse(1L, "v", "l");

        // when
        DataSourceWebResponse result = mapper.toWebDto(src);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.value()).isEqualTo("v");
        assertThat(result.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toWebDto(all): 성공 - 리스트 매핑, null/빈 처리")
    void toWebDto_all_success_and_nullsafe() {
        // given
        AllDataSourcesResponse src = new AllDataSourcesResponse(java.util.List.of(new DataSourceResponse(1L,"v1","l1"), new DataSourceResponse(2L,"v2","l2")));

        // when
        AllDataSourcesWebResponse result = mapper.toWebDto(src);
        AllDataSourcesWebResponse nullSafe1 = mapper.toWebDto((AllDataSourcesResponse) null);
        AllDataSourcesWebResponse nullSafe2 = mapper.toWebDto(new AllDataSourcesResponse(null));

        // then
        assertThat(result.dataSources()).hasSize(2);
        assertThat(nullSafe1.dataSources()).isEmpty();
        assertThat(nullSafe2.dataSources()).isEmpty();
    }
}
