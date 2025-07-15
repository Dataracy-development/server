package com.dataracy.modules.common.util;


import com.dataracy.modules.data.application.dto.response.MetadataParseResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.util.*;

public class FileParsingUtil {

    public static MetadataParseResponse parse(InputStream inputStream, String filename) throws IOException {
        if (filename.endsWith(".csv")) {
            return parseCsv(inputStream);
        } else if (filename.endsWith(".xlsx")) {
            return parseXlsx(inputStream);
        } else if (filename.endsWith(".json")) {
            return parseJson(inputStream);
        }
        throw new IllegalArgumentException("지원하지 않는 파일 형식: " + filename);
    }

    private static MetadataParseResponse parseCsv(InputStream is) throws IOException {
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()               // 첫 줄을 헤더로 사용
                .setSkipHeaderRecord(true) // 헤더 라인은 스킵
                .build();

        CSVParser parser = format.parse(new InputStreamReader(is));
        List<Map<String, String>> preview = new ArrayList<>();
        int rowCount = 0;

        for (var record : parser) {
            if (rowCount++ >= 10) break;
            Map<String, String> row = new LinkedHashMap<>();
            for (String header : parser.getHeaderMap().keySet()) {
                row.put(header, record.get(header));
            }
            preview.add(row);
        }

        return new MetadataParseResponse(rowCount, parser.getHeaderMap().size(), toJson(preview), score(rowCount));
    }

    private static MetadataParseResponse parseXlsx(InputStream is) throws IOException {
        var wb = WorkbookFactory.create(is);
        var sheet = wb.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

        List<Map<String, String>> preview = new ArrayList<>();
        for (int i = 1; i <= Math.min(10, rowCount - 1); i++) {
            var row = sheet.getRow(i);
            Map<String, String> map = new LinkedHashMap<>();
            for (int j = 0; j < colCount; j++) {
                map.put("col" + j, row.getCell(j).toString());
            }
            preview.add(map);
        }

        return new MetadataParseResponse(rowCount - 1, colCount, toJson(preview), score(rowCount));
    }

    private static MetadataParseResponse parseJson(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(is);

        if (!node.isArray()) throw new IllegalArgumentException("루트 노드는 배열이어야 합니다.");
        int rowCount = node.size();
        int colCount = node.get(0).size();

        List<Map<String, Object>> preview = new ArrayList<>();
        for (int i = 0; i < Math.min(10, rowCount); i++) {
            Map<String, Object> row = mapper.convertValue(node.get(i), new TypeReference<Map<String, Object>>() {});
            preview.add(row);
        }

        return new MetadataParseResponse(rowCount, colCount, toJson(preview), score(rowCount));
    }

    private static String toJson(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private static int score(int rowCount) {
        return Math.min(100, rowCount / 10); // 예시 점수
    }
}

