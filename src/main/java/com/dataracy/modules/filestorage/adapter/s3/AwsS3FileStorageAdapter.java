package com.dataracy.modules.filestorage.adapter.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import com.dataracy.modules.filestorage.domain.exception.S3UploadException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AwsS3FileStorageAdapter implements FileStoragePort {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket:}")
    private String bucket;

    /**
     * 지정된 S3 키에 파일을 업로드하고 해당 파일의 공개 URL을 반환합니다.
     *
     * @param key S3에 저장할 파일의 전체 경로 및 파일명
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
            LoggerFactory.common().logError("S3 업로드 실패", "S3 업로드 중 에러가 발생하였습니다.", e);
            throw new S3UploadException("S3 업로드 실패", e);
        }

        return getUrl(key);
    }

    /**
     * S3 파일의 공개 URL을 이용해 파일을 다운로드하고, 전체 내용을 담은 InputStream으로 반환합니다.
     *
     * @param fileUrl 다운로드할 S3 파일의 공개 URL
     * @return 파일 전체 데이터를 포함하는 InputStream
     * @throws S3UploadException 파일 다운로드에 실패한 경우 발생
     */
    @Override
    public InputStream download(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            S3Object s3Object = amazonS3.getObject(bucket, key);
            // S3Object의 내용을 완전히 읽어서 별도의 InputStream으로 반환
            try (InputStream s3InputStream = s3Object.getObjectContent()) {
                return new ByteArrayInputStream(s3InputStream.readAllBytes());
            }
        } catch (Exception e) {
            LoggerFactory.common().logError("S3 파일 다운로드 실패", "S3 파일 다운로드 중 에러가 발생하였습니다.", e);
            throw new S3UploadException("S3 다운로드 실패", e);
        }
    }

    /**
     * 주어진 S3 파일 URL에서 오브젝트 키를 추출하여 해당 파일을 S3 버킷에서 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 전체 S3 URL
     * @throws S3UploadException S3 파일 삭제 중 오류가 발생한 경우 발생합니다.
     */
    @Override
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        try {
            amazonS3.deleteObject(bucket, key);
        } catch (Exception e) {
            LoggerFactory.common().logError("S3 파일 삭제 실패", "S3 파일 삭제 중 에러가 발생하였습니다.", e);
            throw new S3UploadException("S3 파일 삭제 실패", e);
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
     * @throws S3UploadException URL이 올바른 S3 버킷 경로로 시작하지 않거나 키 추출에 실패한 경우 발생합니다.
     */
    private String extractKeyFromUrl(String url) {
        // https://bucket.s3.region.amazonaws.com/key... 에서 key 부분만 추출
        try {
            String hostPrefix = amazonS3.getUrl(bucket, "").toString(); // 끝에 "/" 있음
            if (!url.startsWith(hostPrefix)) {
                LoggerFactory.common().logWarning("S3 URL 형식 오류", "S3 URL 형식이 잘못되었습니다.");
                throw new S3UploadException("S3 URL 형식이 잘못되었습니다: " + url);
            }
            return url.substring(hostPrefix.length());
        } catch (Exception e) {
            LoggerFactory.common().logError("S3 URL 추출 실패", "S3 URL 추출 중 에러가 발생하였습니다.");
            throw new S3UploadException("S3 URL 추출 실패: " + url, e);
        }
    }

    /**
     * S3 버킷 이름이 비어 있거나 공백인지 검증합니다.
     *
     * 애플리케이션 초기화 시 S3 버킷 이름이 올바르게 설정되지 않은 경우 예외를 발생시켜 잘못된 환경 구성을 방지합니다.
     *
     * @throws S3UploadException S3 버킷 이름이 비어 있거나 공백일 때 발생합니다.
     */
    @PostConstruct
    public void validateProperties() {
        if (bucket.isBlank()) {
            LoggerFactory.common().logWarning("AWS S3 버켓 설정 오류", "AWS S3 버켓 설정이 올바르지 않습니다.");
            throw new S3UploadException("AWS S3 버켓 설정이 올바르지 않습니다.");
        }
    }

    /****
     * 지정된 파일 URL과 만료 시간을 기준으로 S3 객체에 대한 Pre-Signed URL을 반환합니다.
     *
     * @param fileUrl Pre-Signed URL을 생성할 S3 파일의 전체 URL
     * @param expirationSeconds Pre-Signed URL의 만료 시간(초)
     * @return 생성된 Pre-Signed URL 문자열
     * @throws S3UploadException Pre-Signed URL 생성에 실패한 경우 발생
     */
    @Override
    public String getPreSignedUrl(String fileUrl, int expirationSeconds) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            Date expiration = new Date(System.currentTimeMillis() + (expirationSeconds * 1000L));

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);

            URL preSignedUrl = amazonS3.generatePresignedUrl(request);
            return preSignedUrl.toString();
        } catch (Exception e) {
            LoggerFactory.common().logError("S3 PreSigned URL 생성 실패", "S3 PreSigned URL 생성 중 에러가 발생하였습니다.");
            throw new S3UploadException("S3 PreSigned URL 생성 실패", e);
        }
    }
}
