package com.dataracy.modules.imagestorage.adapter.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dataracy.modules.imagestorage.application.port.out.ImageStoragePort;
import com.dataracy.modules.imagestorage.domain.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AwsS3ImageStorageAdapter implements ImageStoragePort {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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

    @Override
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        amazonS3.deleteObject(bucket, key);
    }

    @Override
    public String getUrl(String key) {
        return amazonS3.getUrl(bucket, key).toString();
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf(bucket) + bucket.length() + 1);
    }
}
