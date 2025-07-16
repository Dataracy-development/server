package com.dataracy.modules.data.application.service.command;

import com.dataracy.modules.common.util.FileParsingUtil;
import com.dataracy.modules.data.application.dto.request.MetadataParseRequest;
import com.dataracy.modules.data.application.dto.response.MetadataParseResponse;
import com.dataracy.modules.data.application.port.in.MetadataParseUseCase;
import com.dataracy.modules.data.application.port.out.DataMetadataRepositoryPort;
import com.dataracy.modules.data.application.port.out.DataRepositoryPort;
import com.dataracy.modules.data.domain.exception.DataException;
import com.dataracy.modules.data.domain.model.Data;
import com.dataracy.modules.data.domain.model.DataMetadata;
import com.dataracy.modules.data.domain.status.DataErrorStatus;
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
    private final DataRepositoryPort dataRepositoryPort;

    @Override
    public void parseAndSaveMetadata(MetadataParseRequest request) {
        try (InputStream inputStream = fileStoragePort.download(request.fileUrl())) {
            MetadataParseResponse response = FileParsingUtil.parse(inputStream, request.originalFilename());
            DataMetadata metadata = DataMetadata.toDomain(
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
