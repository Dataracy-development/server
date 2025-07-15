package com.dataracy.modules.data.application.port.in;

public interface AnalyzeUploadedDataUseCase {
    void analyze(Long dataId, String fileUrl, String filename);
}
