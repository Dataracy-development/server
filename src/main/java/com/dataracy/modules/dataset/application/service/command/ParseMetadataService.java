package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.FileParsingUtil;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;
import com.dataracy.modules.dataset.application.dto.response.metadata.ParsedMetadataResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabels;
import com.dataracy.modules.dataset.application.port.in.command.metadata.ParseMetadataUseCase;
import com.dataracy.modules.dataset.application.port.out.command.create.CreateMetadataPort;
import com.dataracy.modules.dataset.application.port.out.indexing.IndexDataPort;
import com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ParseMetadataService implements ParseMetadataUseCase {
    private final FileStoragePort fileStoragePort;

    private final CreateMetadataPort metadataRepositoryPort;
    private final FindDataPort findDataPort;
    private final IndexDataPort indexDataPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;

    /**
     * 파일을 다운로드하여 메타데이터를 추출하고, 해당 데이터셋에 저장한 뒤 검색 색인에 반영합니다.
     *
     * 파일 URL과 원본 파일명을 기반으로 파일을 파싱하여 행/열 수 및 미리보기 정보를 포함한 메타데이터를 생성하고, 데이터 ID에 연결해 저장합니다.
     * 이후 데이터와 관련 라벨 정보를 조회하여 검색 색인 문서를 생성하고, 이를 검색 시스템에 색인합니다.
     * 파싱, 저장, 색인 과정에서 발생하는 모든 오류는 예외로 던지지 않고 로그로만 처리됩니다.
     *
     * @param request 메타데이터 파싱 및 저장에 필요한 파일 URL, 원본 파일명, 데이터 ID를 포함한 요청 객체
     */
    @Override
    @Transactional
    public void parseAndSaveMetadata(ParseMetadataRequest request) {
        Instant startTime = LoggerFactory.service().logStart("ParseMetadataUseCase", "데이터셋 파일을 파싱하고 내용 저장 서비스 시작 dataId=" + request.dataId());
        try (InputStream inputStream = fileStoragePort.download(request.fileUrl())) {
            ParsedMetadataResponse response = FileParsingUtil.parse(inputStream, request.originalFilename());
            DataMetadata metadata = DataMetadata.of(
                    null,
                    response.rowCount(),
                    response.columnCount(),
                    response.previewJson()
            );

            metadataRepositoryPort.saveMetadata(request.dataId(), metadata);

            // 색인을 위한 데이터 조회
            Data data = findDataPort.findDataById(request.dataId())
                    .orElseThrow(() -> {
                        LoggerFactory.service().logWarning("ParseMetadataUseCase", "해당 데이터셋이 존재하지 않습니다. dataId=" + request.dataId());
                        return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                    });
            String topicLabel = getTopicLabelFromIdUseCase.getLabelById(data.getTopicId());
            String dataSourceLabel = getDataSourceLabelFromIdUseCase.getLabelById(data.getDataSourceId());
            String dataTypeLabel = getDataTypeLabelFromIdUseCase.getLabelById(data.getDataTypeId());
            String username = findUsernameUseCase.findUsernameById(data.getUserId());
            String userProfileImageUrl = findUserThumbnailUseCase.findUserThumbnailById(data.getUserId());
            DataLabels dataLabels = new DataLabels(topicLabel, dataSourceLabel, dataTypeLabel, username, userProfileImageUrl);

            // Elasticsearch 색인
            DataSearchDocument document = DataSearchDocument.from(data, metadata, dataLabels);
            indexDataPort.index(document);
        } catch (IOException e) {
            LoggerFactory.service().logException("ParseMetadataUseCase", "파일 다운로드 또는 파싱 실패", e);
        } catch (DataException e) {
            LoggerFactory.service().logException("ParseMetadataUseCase", "데이터 조회 실패", e);
        } catch (Exception e) {
            LoggerFactory.service().logException("ParseMetadataUseCase", "예상치 못한 오류 발생", e);
        }
        LoggerFactory.service().logSuccess("ParseMetadataUseCase", "데이터셋 파일을 파싱하고 내용 저장 서비스 종료. dataId=" + request.dataId(), startTime);
    }
}
