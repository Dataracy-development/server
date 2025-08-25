package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataTypeWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataTypeWebMapperTest {
    private final DataTypeWebMapper mapper = new DataTypeWebMapper();

    @Test
    @DisplayName("toWebDto(single): 성공 - 필드 매핑")
    void toWebDto_single_success() {
        // given
        DataTypeResponse src = new DataTypeResponse(1L, "v", "l");

        // when
        DataTypeWebResponse result = mapper.toWebDto(src);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.value()).isEqualTo("v");
        assertThat(result.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toWebDto(all): 성공 - 리스트 매핑, null/빈 처리")
    void toWebDto_all_success_and_nullsafe() {
        // given
        AllDataTypesResponse src = new AllDataTypesResponse(java.util.List.of(new DataTypeResponse(1L,"v1","l1"), new DataTypeResponse(2L,"v2","l2")));

        // when
        AllDataTypesWebResponse result = mapper.toWebDto(src);
        AllDataTypesWebResponse nullSafe1 = mapper.toWebDto((AllDataTypesResponse) null);
        AllDataTypesWebResponse nullSafe2 = mapper.toWebDto(new AllDataTypesResponse(null));

        // then
        assertThat(result.dataTypes()).hasSize(2);
        assertThat(nullSafe1.dataTypes()).isEmpty();
        assertThat(nullSafe2.dataTypes()).isEmpty();
    }
}
