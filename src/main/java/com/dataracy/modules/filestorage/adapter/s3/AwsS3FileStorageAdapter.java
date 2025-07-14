package com.dataracy.modules.filestorage.adapter.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import com.dataracy.modules.filestorage.domain.exception.S3UploadException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3FileStorageAdapter implements FileStoragePort {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket:}")
    private String bucket;

    /**
     * 지정된 S3 키에 파일을 업로드하고 업로드된 파일의 공개 URL을 반환합니다.
     *
     * @param key S3에 저장할 파일의 전체 키(경로 및 파일명)
     * @param file 업로드할 파일
     * @return 업로드된 파일의 S3 공개 URL
     * @throws S3UploadException 파일 업로드 중 입출력 오류가 발생한 경우
     */
    @Override
    public String upload(String key, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, key, inputStream, metadata));
        } catch (IOException e) {
            throw new S3UploadException("S3 업로드 실패", e);
        }

        return getUrl(key);
    }

    /**
     * 주어진 파일 URL에서 S3 오브젝트 키를 추출하여 해당 파일을 S3 버킷에서 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 전체 S3 URL
     */
    @Override
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        try {
            amazonS3.deleteObject(bucket, key);
        } catch (Exception e) {
            log.warn("S3 파일 삭제 실패: {}", fileUrl, e);
            // 비즈니스 요구사항에 따라 예외를 던질지 결정
        }
    }

    /**
     * 지정된 S3 객체 키에 대한 공개 접근 가능한 URL을 반환합니다.
     *
     * @param key S3 객체의 고유 키
     * @return 해당 S3 객체의 공개 URL
     */
    @Override
    public String getUrl(String key) {
        return amazonS3.getUrl(bucket, key).toString();
    }

    /**
     * S3 파일의 전체 URL에서 객체 키를 추출합니다.
     *
     * @param url S3 파일의 전체 URL
     * @return S3 객체 키
     * @throws IllegalArgumentException URL이 올바른 S3 버킷 경로로 시작하지 않거나 키 추출에 실패한 경우 발생합니다.
     */
    private String extractKeyFromUrl(String url) {
        // https://bucket.s3.region.amazonaws.com/key... 에서 key 부분만 추출
        try {
            String hostPrefix = amazonS3.getUrl(bucket, "").toString(); // 끝에 "/" 있음
            if (!url.startsWith(hostPrefix)) {
                throw new IllegalArgumentException("S3 URL 형식이 잘못되었습니다: " + url);
            }
            return url.substring(hostPrefix.length());
        } catch (Exception e) {
            throw new IllegalArgumentException("S3 URL 추출 실패: " + url, e);
        }
    }


    /**
     * S3 버킷 이름이 올바르게 설정되었는지 검증합니다.
     *
     * 버킷 이름이 비어 있으면 애플리케이션 초기화 시 예외를 발생시킵니다.
     *
     * @throws IllegalStateException 버킷 이름이 비어 있을 경우 발생합니다.
     */
    @PostConstruct
    public void validateProperties() {
        if (bucket.isBlank()) {
            throw new IllegalStateException("AWS S3 버켓 설정이 올바르지 않습니다.");
        }
    }
}
