package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.util.FileParsingUtil;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.request.MetadataParseRequest;
import com.dataracy.modules.dataset.application.dto.response.DataLabels;
import com.dataracy.modules.dataset.application.dto.response.MetadataParseResponse;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataIndexingPort;
import com.dataracy.modules.dataset.application.port.in.MetadataParseUseCase;
import com.dataracy.modules.dataset.application.port.out.DataMetadataRepositoryPort;
import com.dataracy.modules.dataset.application.port.out.DataRepositoryPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.profile.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataParseService implements MetadataParseUseCase {

    private final FileStoragePort fileStoragePort;
    private final DataMetadataRepositoryPort metadataRepositoryPort;
    private final DataRepositoryPort dataRepositoryPort;
    private final FindUsernameUseCase findUsernameUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;
    private final DataIndexingPort dataIndexingPort;

    /**
     * 파일을 다운로드하여 메타데이터를 추출하고, 해당 데이터를 데이터 ID에 연결해 저장 및 검색 색인에 반영합니다.
     *
     * 파일 URL과 원본 파일명을 기반으로 파일을 파싱하여 행/열 수, 미리보기 정보를 포함한 메타데이터를 생성하고 저장합니다.
     * 이후 관련 데이터와 라벨 정보를 조회하여 검색 색인 문서를 생성하고, 이를 검색 시스템에 색인합니다.
     * 파싱, 저장, 색인 과정에서 발생하는 모든 오류는 예외로 던지지 않고 로그로만 처리됩니다.
     *
     * @param request 메타데이터 파싱 및 저장에 필요한 파일 URL, 원본 파일명, 데이터 ID를 포함한 요청 객체
     */
    @Override
    @Transactional
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

            // 색인을 위한 데이터 조회
            Data data = dataRepositoryPort.findDataById(request.dataId())
                    .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
            String topicLabel = getTopicLabelFromIdUseCase.getLabelById(data.getTopicId());
            String dataSourceLabel = getDataSourceLabelFromIdUseCase.getLabelById(data.getDataSourceId());
            String dataTypeLabel = getDataTypeLabelFromIdUseCase.getLabelById(data.getDataTypeId());
            String username = findUsernameUseCase.findUsernameById(data.getUserId());
            DataLabels dataLabels = new DataLabels(topicLabel, dataSourceLabel, dataTypeLabel, username);

            // Elasticsearch 색인
            DataSearchDocument document = DataSearchDocument.from(data, metadata, dataLabels);
            dataIndexingPort.index(document);

            log.info("새 데이터 인덱싱 완료: dataId={}", request.dataId());

        } catch (IOException e) {
            log.error("파일 다운로드 또는 파싱 실패: dataId={}, url={}", request.dataId(), request.fileUrl(), e);
        } catch (DataException e) {
            log.error("데이터 조회 실패: dataId={}", request.dataId(), e);
        } catch (Exception e) {
            // 업로드 이후 비동기 처리로 실패시 예외 처리가 아닌 로그처리
            log.error("예상치 못한 오류 발생: dataId={}, url={}", request.dataId(), request.fileUrl(), e);
        }
    }
}
