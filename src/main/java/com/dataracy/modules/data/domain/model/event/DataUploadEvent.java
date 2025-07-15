package com.dataracy.modules.data.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataUploadEvent {
    private Long dataId;
    private String fileUrl;
    private String originalFilename;
}
