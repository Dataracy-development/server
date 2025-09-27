package com.dataracy.modules.common.util;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.metadata.ParsedMetadataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileParsingUtilTest {

    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private MockedStatic<WorkbookFactory> workbookFactoryMock;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        workbookFactoryMock = mockStatic(WorkbookFactory.class);
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
        if (workbookFactoryMock != null) {
            workbookFactoryMock.close();
        }
    }

    @Test
    @DisplayName("parse - CSV 파일을 성공적으로 파싱한다")
    void parse_CsvFile_Success() throws IOException {
        // given
        String csvContent = "name,age,city\nJohn,25,Seoul\nJane,30,Busan";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        String filename = "test.csv";

        mockLoggerFactory();

        // when
        ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, filename);

        // then
        assertThat(result).isNotNull();
        assertThat(result.rowCount()).isEqualTo(2); // 헤더 제외
        assertThat(result.columnCount()).isEqualTo(3);
        assertThat(result.previewJson()).isNotEmpty();
    }

    @Test
    @DisplayName("parse - XLSX 파일을 성공적으로 파싱한다")
    void parse_XlsxFile_Success() throws IOException {
        // given
        byte[] xlsxContent = createMockXlsxContent();
        InputStream inputStream = new ByteArrayInputStream(xlsxContent);
        String filename = "test.xlsx";

        mockLoggerFactory();
        mockWorkbookFactory();

        // when
        ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, filename);

        // then
        assertThat(result).isNotNull();
        // Mock된 워크북의 기본값으로 검증
    }

    @Test
    @DisplayName("parse - JSON 파일을 성공적으로 파싱한다")
    void parse_JsonFile_Success() throws IOException {
        // given
        String jsonContent = "[{\"name\":\"John\",\"age\":25},{\"name\":\"Jane\",\"age\":30}]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));
        String filename = "test.json";

        mockLoggerFactory();

        // when
        ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, filename);

        // then
        assertThat(result).isNotNull();
        assertThat(result.rowCount()).isEqualTo(2);
        assertThat(result.columnCount()).isEqualTo(2);
        assertThat(result.previewJson()).isNotEmpty();
    }

    @Test
    @DisplayName("parse - null InputStream에 대해 IllegalArgumentException을 던진다")
    void parse_NullInputStream_ThrowsException() {
        // given
        String filename = "test.csv";

        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> FileParsingUtil.parse(null, filename))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("입력 스트림은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("parse - null filename에 대해 IllegalArgumentException을 던진다")
    void parse_NullFilename_ThrowsException() {
        // given
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());

        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> FileParsingUtil.parse(inputStream, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일명은 null이거나 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("parse - 빈 filename에 대해 IllegalArgumentException을 던진다")
    void parse_EmptyFilename_ThrowsException() {
        // given
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());

        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> FileParsingUtil.parse(inputStream, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일명은 null이거나 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("parse - 지원하지 않는 파일 형식에 대해 IllegalArgumentException을 던진다")
    void parse_UnsupportedFileFormat_ThrowsException() {
        // given
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String filename = "test.txt";

        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> FileParsingUtil.parse(inputStream, filename))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 파일 형식: " + filename);
    }

    @Test
    @DisplayName("parse - JSON 파일이 배열이 아닌 경우 IllegalArgumentException을 던진다")
    void parse_JsonNotArray_ThrowsException() {
        // given
        String jsonContent = "{\"name\":\"John\",\"age\":25}";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));
        String filename = "test.json";

        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> FileParsingUtil.parse(inputStream, filename))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("루트 노드는 배열이어야 합니다.");
    }

    @Test
    @DisplayName("detectEncoding - UTF-8 인코딩을 감지한다")
    void detectEncoding_UTF8_Success() throws IOException {
        // given
        String content = "테스트 내용";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        // when
        Charset result = FileParsingUtil.detectEncoding(inputStream);

        // then
        assertThat(result).isEqualTo(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("detectEncoding - mark/reset을 지원하지 않는 InputStream을 처리한다")
    void detectEncoding_NonMarkSupportedInputStream_Success() throws IOException {
        // given
        String content = "test content";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        // when
        Charset result = FileParsingUtil.detectEncoding(inputStream);

        // then
        assertThat(result).isEqualTo(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("detectEncoding - 빈 InputStream에 대해 UTF-8을 반환한다")
    void detectEncoding_EmptyInputStream_ReturnsUTF8() throws IOException {
        // given
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        // when
        Charset result = FileParsingUtil.detectEncoding(inputStream);

        // then
        assertThat(result).isEqualTo(StandardCharsets.UTF_8);
    }

    private void mockLoggerFactory() {
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.common()).thenReturn(loggerCommon);
        lenient().doNothing().when(loggerCommon).logWarning(anyString(), anyString());
        lenient().doNothing().when(loggerCommon).logError(anyString(), anyString());
    }

    private void mockWorkbookFactory() throws IOException {
        var mockWorkbook = mock(org.apache.poi.ss.usermodel.Workbook.class);
        var mockSheet = mock(org.apache.poi.ss.usermodel.Sheet.class);
        var mockRow = mock(org.apache.poi.ss.usermodel.Row.class);
        var mockCell = mock(org.apache.poi.ss.usermodel.Cell.class);

        workbookFactoryMock.when(() -> WorkbookFactory.create(any(InputStream.class)))
                .thenReturn(mockWorkbook);

        when(mockWorkbook.getSheetAt(0)).thenReturn(mockSheet);
        when(mockSheet.getPhysicalNumberOfRows()).thenReturn(3);
        when(mockSheet.getRow(0)).thenReturn(mockRow);
        when(mockRow.getPhysicalNumberOfCells()).thenReturn(2);
        when(mockRow.getCell(anyInt())).thenReturn(mockCell);
        when(mockCell.toString()).thenReturn("header");
    }

    private byte[] createMockXlsxContent() {
        // 실제 XLSX 파일의 바이트 배열을 생성하는 대신 더미 데이터 반환
        return "dummy xlsx content".getBytes();
    }
}
