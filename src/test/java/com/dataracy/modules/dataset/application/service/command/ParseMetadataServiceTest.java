package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;
import com.dataracy.modules.dataset.application.port.out.command.create.CreateMetadataPort;
import com.dataracy.modules.dataset.application.port.out.indexing.IndexDataPort;
import com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ParseMetadataServiceTest {

    @InjectMocks
    private ParseMetadataService service;

    @Mock private FileStoragePort fileStoragePort;
    @Mock private CreateMetadataPort createMetadataPort;
    @Mock private FindDataPort findDataPort;
    @Mock private IndexDataPort indexDataPort;
    @Mock private FindUsernameUseCase findUsernameUseCase;
    @Mock private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    @Mock private GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    @Mock private GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;

    @Test
    void parseAndSaveMetadataShouldIndexWhenValid() throws Exception {
        ParseMetadataRequest req = new ParseMetadataRequest(1L,"url","file.csv");
        InputStream stream = new ByteArrayInputStream("col1,col2\\n1,2".getBytes());
        given(fileStoragePort.download("url")).willReturn(stream);
        Data mockData = Data.of(1L,"title",2L,3L,4L,5L,null,null,
                "d","g","f","t",1,1L,null, LocalDateTime.now());
        given(findDataPort.findDataById(1L)).willReturn(Optional.of(mockData));
        given(getTopicLabelFromIdUseCase.getLabelById(any())).willReturn("topic");
        given(getDataSourceLabelFromIdUseCase.getLabelById(any())).willReturn("src");
        given(getDataTypeLabelFromIdUseCase.getLabelById(any())).willReturn("type");
        given(findUsernameUseCase.findUsernameById(any())).willReturn("user");

        service.parseAndSaveMetadata(req);

        then(createMetadataPort).should().saveMetadata(eq(1L), any());
        then(indexDataPort).should().index(any());
    }

    @Test
    void parseAndSaveMetadataShouldLogWhenNotFound() throws Exception {
        ParseMetadataRequest req = new ParseMetadataRequest(99L,"url","file.csv");
        InputStream stream = new ByteArrayInputStream("col1,col2".getBytes());
        given(fileStoragePort.download("url")).willReturn(stream);
        given(findDataPort.findDataById(99L)).willReturn(Optional.empty());

        // 예외는 내부 catch에서 처리, 그냥 로그만 남기고 정상 종료
        service.parseAndSaveMetadata(req);

        then(indexDataPort).shouldHaveNoInteractions();
    }
}
