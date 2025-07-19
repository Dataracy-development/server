package com.dataracy.modules.data.application.service.command;

import com.dataracy.modules.common.util.FileParsingUtil;
import com.dataracy.modules.data.application.dto.request.MetadataParseRequest;
import com.dataracy.modules.data.application.dto.response.MetadataParseResponse;
import com.dataracy.modules.data.application.port.in.MetadataParseUseCase;
import com.dataracy.modules.data.application.port.out.DataMetadataRepositoryPort;
import com.dataracy.modules.data.domain.model.DataMetadata;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataParseService implements MetadataParseUseCase {

    private final FileStoragePort fileStoragePort;
    private final DataMetadataRepositoryPort metadataRepositoryPort;

    /**
     * 파일 URL과 원본 파일명을 기반으로 파일을 파싱하여 메타데이터를 추출하고, 해당 데이터를 저장합니다.
     *
     * 파일 파싱 중 오류가 발생하면 예외를 던지지 않고 로그로만 처리합니다.
     *
     * @param request 메타데이터 파싱 및 저장에 필요한 파일 URL, 원본 파일명, 데이터 ID 정보를 포함한 요청 객체
     */
    @Override
    public void parseAndSaveMetadata(MetadataParseRequest request) {
        try (InputStream inputStream = fileStoragePort.download(request.fileUrl())) {
            MetadataParseResponse response = FileParsingUtil.parse(inputStream, request.originalFilename());
            DataMetadata metadata = DataMetadata.of(
                    null,
                    response.rowCount(),
                    response.columnCount(),
                    response.previewJson()
            );

            metadataRepositoryPort.saveMetadata(request.dataId(), metadata);
            log.info("메타데이터 저장 완료: dataId={}, row={}, column={}",
                    request.dataId(), response.rowCount(), response.columnCount());

        } catch (Exception e) {
            // 업로드 이후 비동기 처리로 실패시 예외 처리가 아닌 로그처리
            log.error("메타데이터 파싱 실패: dataId={}, url={}", request.dataId(), request.fileUrl(), e);
        }
    }
}
