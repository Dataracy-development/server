package com.dataracy.modules.filestorage.adapter.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import com.dataracy.modules.filestorage.domain.exception.S3UploadException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AwsS3FileStorageAdapter implements FileStoragePort {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket:}")
    private String bucket;

    /**
     * 지정된 디렉터리에 파일을 업로드하고 S3에 저장된 파일의 공개 URL을 반환합니다.
     *
     * @param directory S3에 파일을 저장할 디렉터리 경로
     * @param file 업로드할 파일
     * @return 업로드된 파일의 S3 공개 URL
     * @throws S3UploadException 파일 업로드 중 입출력 오류가 발생한 경우
     */
    @Override
    public String upload(String directory, MultipartFile file) {
        String key = directory + "/" + UUID.randomUUID();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, key, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
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
        amazonS3.deleteObject(bucket, key);
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
     * S3 파일 URL에서 객체 키를 추출합니다.
     *
     * @param url S3 파일의 전체 URL
     * @return 추출된 S3 객체 키
     * @throws IllegalArgumentException URL에 버킷명이 없거나 키가 존재하지 않을 경우 발생합니다.
     */
    private String extractKeyFromUrl(String url) {
        int bucketIndex = url.indexOf(bucket);
        if (bucketIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 URL format: " + url);
        }
        int keyStartIndex = bucketIndex + bucket.length() + 1;
        if (keyStartIndex >= url.length()) {
            throw new IllegalArgumentException("No key found in S3 URL: " + url);
        }
        return url.substring(keyStartIndex);
    }

    @PostConstruct
    public void validateProperties() {
        if (bucket.isBlank()) {
            throw new IllegalStateException("AWS S3 버켓 설정이 올바르지 않습니다.");
        }
    }
}
