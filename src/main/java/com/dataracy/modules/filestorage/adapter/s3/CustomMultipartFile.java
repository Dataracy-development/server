package com.dataracy.modules.filestorage.adapter.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {
    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    /**
     * 주어진 파일 데이터와 메타데이터로 메모리 기반의 CustomMultipartFile 객체를 생성합니다.
     *
     * @param content           파일의 바이트 배열 데이터. null일 경우 빈 배열로 대체됩니다.
     * @param name              파일의 이름. null일 경우 빈 문자열로 대체됩니다.
     * @param originalFilename  업로드된 원본 파일명. null일 경우 빈 문자열로 대체됩니다.
     * @param contentType       파일의 MIME 타입. null일 경우 "application/octet-stream"으로 대체됩니다.
     */
    public CustomMultipartFile(byte[] content, String name, String originalFilename, String contentType) {
        this.content = content != null ? content.clone() : new byte[0];
        this.name = name != null ? name : "";
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType != null ? contentType : "application/octet-stream";
    }

    /**
     * 이 파일의 이름을 반환합니다.
     *
     * @return 파일의 이름
     */
    @Override public String getName() {
        return name;
    }

    /**
     * 업로드된 파일의 원본 파일명을 반환합니다.
     *
     * @return 업로드 시 사용된 원본 파일명
     */
    @Override public String getOriginalFilename() {
        return originalFilename;
    }

    /**
     * 파일의 MIME 콘텐츠 타입을 반환합니다.
     *
     * @return 파일의 MIME 타입 문자열
     */
    @Override public String getContentType() {
        return contentType;
    }

    /**
     * 파일의 내용이 비어 있는지 확인합니다.
     *
     * @return 파일 내용이 없으면 true, 그렇지 않으면 false입니다.
     */
    @Override public boolean isEmpty() {
        return content.length == 0;
    }

    /**
     * 파일의 크기를 바이트 단위로 반환합니다.
     *
     * @return 파일 데이터의 총 바이트 수
     */
    @Override public long getSize() {
        return content.length;
    }

    /**
     * 파일의 내용을 복제된 바이트 배열로 반환합니다.
     *
     * @return 파일 데이터의 복제본 바이트 배열
     */
    @Override public byte[] getBytes() {
        return content.clone();
    }

    /**
     * 파일 데이터에 대한 새로운 ByteArrayInputStream을 반환합니다.
     *
     * @return 파일 내용을 읽기 위한 입력 스트림
     */
    @Override public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    /**
     * 파일의 내용을 지정된 파일로 복사합니다.
     *
     * @param dest 복사 대상이 되는 파일
     * @throws IOException 파일 쓰기 중 입출력 오류가 발생한 경우
     * @throws IllegalStateException 파일 전송이 허용되지 않는 상태인 경우
     */
    @Override public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}
