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
        try (InputStream inputStream = originalImage.getInputStream();
             ByteArrayOutputStream thumbnailOutput = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat("jpg")
                    .toOutputStream(thumbnailOutput);

            return thumbnailOutput;

        } catch (IOException e) {
            throw new RuntimeException("썸네일 생성 실패", e);
        }
    }
}
