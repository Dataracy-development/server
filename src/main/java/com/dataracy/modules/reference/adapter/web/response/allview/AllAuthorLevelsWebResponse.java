package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.AuthorLevelWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "작성자 유형 리스트 조회 응답")
public record AllAuthorLevelsWebResponse(List<AuthorLevelWebResponse> authorLevels) {
}
