/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.filestorage.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.filestorage.adapter.web.response.GetPreSignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;

class FileDownloadWebMapperTest {

  @Test
  @DisplayName("일시적으로 파일 다운로드 가능한 경로 반환")
  void toWebDtoShouldMapFields() {
    // given
    FileDownloadWebMapper mapper = new FileDownloadWebMapper();
    GetPreSignedUrlResponse dto = new GetPreSignedUrlResponse("http://signed");

    // when
    GetPreSignedUrlWebResponse web = mapper.toWebDto(dto);

    // then
    assertThat(web.preSignedUrl()).isEqualTo("http://signed");
  }
}
