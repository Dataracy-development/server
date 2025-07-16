package com.dataracy.modules.common.util;

import com.dataracy.modules.data.application.dto.response.MetadataParseResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileParsingUtil {

    private static final int PREVIEW_LIMIT = 5;

    public static MetadataParseResponse parse(InputStream inputStream, String filename) throws IOException {
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".csv")) {
            return parseCsv(inputStream);
        } else if (lowerName.endsWith(".xlsx")) {
            return parseXlsx(inputStream);
        } else if (lowerName.endsWith(".json")) {
            return parseJson(inputStream);
        }
        throw new IllegalArgumentException("지원하지 않는 파일 형식: " + filename);
    }

    private static MetadataParseResponse parseCsv(InputStream originalInputStream) throws IOException {
        // InputStream 복사: 두 번 읽기 위함
        byte[] data = originalInputStream.readAllBytes();
        InputStream encodingStream = new ByteArrayInputStream(data);
        InputStream parseStream = new ByteArrayInputStream(data);

        Charset charset = detectEncoding(encodingStream);

        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(parseStream, charset));
             CSVParser parser = format.parse(reader)) {

            List<Map<String, String>> preview = new ArrayList<>();
            int rowCount = 0;
            int colCount = parser.getHeaderMap().size();

            for (var record : parser) {
                rowCount++;

                if (preview.size() < PREVIEW_LIMIT) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (String header : parser.getHeaderMap().keySet()) {
                        row.put(header, record.get(header));
                    }
                    preview.add(row);
                }
            }

            return new MetadataParseResponse(
                    rowCount,
                    colCount,
                    toJson(preview)
            );
        }
    }

    private static MetadataParseResponse parseXlsx(InputStream is) throws IOException {
        var wb = WorkbookFactory.create(is);
        var sheet = wb.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

        List<Map<String, String>> preview = new ArrayList<>();
        for (int i = 1; i <= Math.min(PREVIEW_LIMIT, rowCount - 1); i++) {
            var row = sheet.getRow(i);
            if (row == null) continue; // null row 방어

            Map<String, String> map = new LinkedHashMap<>();
            for (int j = 0; j < colCount; j++) {
                var cell = row.getCell(j);
                map.put("col" + j, cell != null ? cell.toString() : "");
            }
            preview.add(map);
        }

        return new MetadataParseResponse(
                rowCount - 1, // header 제외
                colCount,
                toJson(preview)
        );
    }

    private static MetadataParseResponse parseJson(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(is);

        if (!node.isArray()) {
            throw new IllegalArgumentException("루트 노드는 배열이어야 합니다.");
        }

        int rowCount = node.size();
        int colCount = node.get(0).size();

        List<Map<String, Object>> preview = new ArrayList<>();
        for (int i = 0; i < Math.min(PREVIEW_LIMIT, rowCount); i++) {
            Map<String, Object> row = mapper.convertValue(
                    node.get(i),
                    new TypeReference<>() {}
            );
            preview.add(row);
        }

        return new MetadataParseResponse(
                rowCount,
                colCount,
                toJson(preview)
        );
    }

    private static String toJson(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    /**
     * 파일 인코딩을 자동 감지하여 Charset을 반환합니다.
     *
     * @param is 인코딩을 감지할 InputStream (주의: markSupported)
     * @return 감지된 Charset 또는 기본 UTF-8
     */
    public static Charset detectEncoding(InputStream is) throws IOException {
        // mark/reset을 위해 InputStream이 지원되어야 함
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }

        is.mark(4096);

        byte[] buf = new byte[4096];
        UniversalDetector detector = new UniversalDetector(null);

        int read;
        while ((read = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, read);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        is.reset(); // 다시 원위치로 되돌림

        return encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
    }
}
