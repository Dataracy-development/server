package com.dataracy.modules.imagestorage.application.port.in;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadUseCase {
    String uploadImage(String directory, MultipartFile image);
    void deleteImage(String imageUrl);
}
