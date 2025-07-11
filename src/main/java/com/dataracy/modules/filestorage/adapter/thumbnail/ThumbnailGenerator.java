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
        try (InputStream is = originalImage.getInputStream(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Thumbnails.of(is).size(width, height).outputFormat("jpg").toOutputStream(os);
            return os;
        } catch (IOException e) {
            throw new RuntimeException("썸네일 생성 실패", e);
        }
    }
}
