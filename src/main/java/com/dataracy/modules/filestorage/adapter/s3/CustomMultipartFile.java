package com.dataracy.modules.filestorage.adapter.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {

    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    /**
     * 새로운 파일 데이터와 메타데이터로 CustomMultipartFile 인스턴스를 생성합니다.
     *
     * @param content           파일의 바이트 배열 데이터. null이면 빈 배열로 초기화됩니다.
     * @param name              파일의 이름. null이면 빈 문자열로 설정됩니다.
     * @param originalFilename  원본 파일명. null이면 빈 문자열로 설정됩니다.
     * @param contentType       MIME 타입. null이면 "application/octet-stream"으로 설정됩니다.
     */
    public CustomMultipartFile(byte[] content, String name, String originalFilename, String contentType) {
        this.content = content != null ? content.clone() : new byte[0];
        this.name = name != null ? name : "";
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType != null ? contentType : "application/octet-stream";
    }

    /**
     * 파일의 이름을 반환합니다.
     *
     * @return 파일의 이름
     */
    @Override public String getName() { return name; }
    /**
     * 업로드된 파일의 원본 파일명을 반환합니다.
     *
     * @return 파일의 원본 이름
     */
    @Override public String getOriginalFilename() { return originalFilename; }
    /**
     * 파일의 MIME 콘텐츠 타입을 반환합니다.
     *
     * @return 파일의 MIME 타입 문자열
     */
    @Override public String getContentType() { return contentType; }
    /**
     * 파일의 내용이 비어 있는지 여부를 반환합니다.
     *
     * @return 파일 내용이 없으면 true, 그렇지 않으면 false
     */
    @Override public boolean isEmpty() { return content.length == 0; }
    /**
     * 파일의 크기를 바이트 단위로 반환합니다.
     *
     * @return 파일 내용의 바이트 크기
     */
    @Override public long getSize() { return content.length; }
    /**
     * 파일의 내용을 복제된 바이트 배열로 반환합니다.
     *
     * @return 파일 데이터의 복제된 바이트 배열
     */
    @Override public byte[] getBytes() { return content.clone(); }
    /**
     * 파일 내용을 읽을 수 있는 새로운 입력 스트림을 반환합니다.
     *
     * @return 파일 데이터에 대한 ByteArrayInputStream 인스턴스
     */
    @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }
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
