package com.dataracy.modules.dataset.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DataUploadEvent {
    private Long dataId;
    private String fileUrl;
    private String originalFilename;
}
