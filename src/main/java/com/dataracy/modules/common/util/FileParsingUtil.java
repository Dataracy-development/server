package com.dataracy.modules.common.util;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.metadata.ParsedMetadataResponse;
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
    private static final String COL_PREFIX = "col";
    private static final int SHEET_INDEX = 0;

    /****
     * 입력 스트림과 파일명을 기반으로 파일 형식을 자동 감지하여(CSV, XLSX, JSON) 행 수, 열 수, 미리보기 데이터를 추출합니다.
     *
     * @param inputStream 파일 데이터가 포함된 입력 스트림
     * @param filename 파일명(확장자를 포함하여 파일 형식 판별에 사용)
     * @return 행 수, 열 수, 미리보기 데이터를 포함하는 ParsedMetadataResponse 객체
     * @throws IllegalArgumentException 입력값이 null이거나 비어 있거나, 지원하지 않는 파일 형식일 때 발생
     * @throws IOException 파일 파싱 중 입출력 오류가 발생할 경우
     */
    public static ParsedMetadataResponse parse(InputStream inputStream, String filename) throws IOException {
        if (inputStream == null) {
            LoggerFactory.common().logWarning("메타데이터 파싱", "입력 스트림은 null일 수 없습니다.");
            throw new IllegalArgumentException("입력 스트림은 null일 수 없습니다.");
        }
        if (filename == null || filename.trim().isEmpty()) {
            LoggerFactory.common().logWarning("메타데이터 파싱", "파일명은 null이거나 비어있을 수 없습니다.");
            throw new IllegalArgumentException("파일명은 null이거나 비어있을 수 없습니다.");
        }

        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".csv")) {
            return parseCsv(inputStream);
        } else if (lowerName.endsWith(".xlsx")) {
            return parseXlsx(inputStream);
        } else if (lowerName.endsWith(".json")) {
            return parseJson(inputStream);
        }

        LoggerFactory.common().logError("메타데이터 파싱", "지원하지 않는 파일 형식: " + filename);
        throw new IllegalArgumentException("지원하지 않는 파일 형식: " + filename);
    }

    /**
     * CSV 파일 입력 스트림을 파싱하여 전체 행 수, 컬럼 수, 미리보기 데이터를 추출합니다.
     *
     * 입력 스트림의 문자 인코딩을 자동 감지한 후, 첫 번째 행을 헤더로 인식하여 데이터를 파싱합니다.
     * 최대 5개의 미리보기 행을 헤더-값 쌍의 리스트로 반환합니다.
     *
     * @param originalInputStream CSV 파일의 입력 스트림
     * @return 전체 행 수, 컬럼 수, 미리보기 데이터(JSON 문자열)를 포함한 ParsedMetadataResponse 객체
     * @throws IOException 스트림 읽기 또는 파싱 중 오류가 발생한 경우
     */
    private static ParsedMetadataResponse parseCsv(InputStream originalInputStream) throws IOException {
        // BufferedInputStream으로 wrap하여 mark/reset 지원
        if (!originalInputStream.markSupported()) {
            originalInputStream = new BufferedInputStream(originalInputStream);
        }

        // 인코딩 감지를 위한 mark 설정
        originalInputStream.mark(8192);
        Charset charset = detectEncoding(originalInputStream);
        originalInputStream.reset();

        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(originalInputStream, charset));
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

            return new ParsedMetadataResponse(
                    rowCount,
                    colCount,
                    toJson(preview)
            );
        }
    }

    /**
     * XLSX 파일의 첫 번째 시트를 파싱하여 행 수, 열 수, 미리보기 데이터를 추출합니다.
     *
     * 첫 번째 행을 헤더로 간주하며, 비어 있는 헤더 셀은 기본 컬럼명으로 대체됩니다. 미리보기 데이터는 최대 5개의 데이터 행(헤더 제외)으로 구성되며, 각 행은 헤더명을 키로 하는 맵 형태로 반환됩니다.
     *
     * @param is XLSX 파일의 입력 스트림
     * @return 행 수(헤더 제외), 열 수, 미리보기 데이터(JSON 문자열)가 포함된 ParsedMetadataResponse 객체
     * @throws IOException 파일 읽기 또는 파싱 중 오류가 발생한 경우
     */
    private static ParsedMetadataResponse parseXlsx(InputStream is) throws IOException {
        try (var wb = WorkbookFactory.create(is)) {
            var sheet = wb.getSheetAt(SHEET_INDEX);
            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount == 0) {
                return new ParsedMetadataResponse(0, 0, toJson(new ArrayList<>()));
            }
            var firstRow = sheet.getRow(0);
            if (firstRow == null) {
                return new ParsedMetadataResponse(0, 0, toJson(new ArrayList<>()));
            }
            int colCount = firstRow.getPhysicalNumberOfCells();

            // 헤더 행 처리 추가
            List<String> headers = new ArrayList<>();
            for (int j = 0; j < colCount; j++) {
                var headerCell = firstRow.getCell(j);
                String headerValue = headerCell != null ? headerCell.toString() : COL_PREFIX + j;
                headers.add(headerValue);
            }

            List<Map<String, String>> preview = new ArrayList<>();
            for (int i = 1; i <= Math.min(PREVIEW_LIMIT, rowCount - 1); i++) {
                var row = sheet.getRow(i);
                if (row == null) continue; // null row 방어

                // 데이터 행 처리 시 헤더 사용
                Map<String, String> map = new LinkedHashMap<>();
                for (int j = 0; j < colCount; j++) {
                    var cell = row.getCell(j);
                    map.put(headers.get(j), cell != null ? cell.toString() : "");
                }
                preview.add(map);
            }

            return new ParsedMetadataResponse(
                    rowCount - 1, // header 제외
                    colCount,
                    toJson(preview)
            );
        }


    }

    /**
     * JSON 입력 스트림에서 행 수, 열 수, 미리보기 데이터를 추출합니다.
     *
     * 입력 스트림의 루트 노드는 반드시 배열이어야 하며, 각 요소의 필드를 기준으로 열 수를 계산하고 최대 5개의 미리보기 데이터를 제공합니다.
     *
     * @param is JSON 데이터를 포함하는 입력 스트림
     * @return 행 수, 열 수, 미리보기 데이터(JSON 문자열)를 포함하는 ParsedMetadataResponse 객체
     * @throws IOException 입력 스트림을 읽거나 파싱할 때 오류가 발생한 경우
     * @throws IllegalArgumentException 루트 노드가 배열이 아닌 경우
     */
    private static ParsedMetadataResponse parseJson(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(is);

        if (!node.isArray()) {
            LoggerFactory.common().logWarning("메타데이터 파싱", "[parseJson] 루트 노드는 배열이어야 합니다.");
            throw new IllegalArgumentException("루트 노드는 배열이어야 합니다.");
        }

        int rowCount = node.size();
        int colCount = 0;
        if (rowCount > 0) {
            colCount = node.get(0).size();
        }

        List<Map<String, Object>> preview = new ArrayList<>();
        for (int i = 0; i < Math.min(PREVIEW_LIMIT, rowCount); i++) {
            Map<String, Object> row = mapper.convertValue(
                    node.get(i),
                    new TypeReference<>() {}
            );
            preview.add(row);
        }

        return new ParsedMetadataResponse(
                rowCount,
                colCount,
                toJson(preview)
        );
    }

    /**
     * 주어진 객체를 JSON 문자열로 변환합니다.
     *
     * @param obj JSON으로 직렬화할 객체
     * @return 변환된 JSON 문자열
     * @throws IOException 직렬화 중 입출력 오류가 발생한 경우
     */
    private static String toJson(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    /**
     * 입력 스트림에서 문자 인코딩을 자동 감지하여 해당 Charset을 반환합니다.
     *
     * 입력 스트림의 처음 최대 4096바이트를 분석하여 UniversalDetector로 인코딩을 감지하며,
     * 감지된 인코딩이 없거나 지원되지 않는 경우 기본적으로 UTF-8 Charset을 반환합니다.
     *
     * @param is 인코딩을 감지할 InputStream (mark/reset 지원 필요)
     * @return 감지된 Charset, 감지 실패 또는 미지원 시 UTF-8
     * @throws IOException 스트림 읽기 또는 reset 중 오류가 발생한 경우
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

        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (Exception e) {
                return StandardCharsets.UTF_8;
            }
        }
        return StandardCharsets.UTF_8;
    }
}
