package com.dataracy.modules.filestorage.adapter.thumbnail;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ThumbnailGenerator {

    public ByteArrayOutputStream createThumbnail(MultipartFile originalImage, int width, int height) {
        if (originalImage == null || originalImage.isEmpty()) {
            throw new IllegalArgumentException("원본 이미지가 필요합니다");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("너비와 높이는 양수여야 합니다");
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (InputStream is = originalImage.getInputStream()) {
            Thumbnails.of(is).size(width, height).outputFormat("jpg").toOutputStream(os);
            return os;
        } catch (IOException e) {
            throw new RuntimeException("썸네일 생성 실패", e);
        }
    }
}
