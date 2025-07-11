package com.dataracy.modules.imagestorage.application.service.command;

import com.dataracy.modules.imagestorage.application.port.in.ImageUploadUseCase;
import com.dataracy.modules.imagestorage.application.port.out.ImageStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageUploadService implements ImageUploadUseCase {

    private final ImageStoragePort imageStoragePort;

    @Override
    public String uploadImage(String directory, MultipartFile image) {
        return imageStoragePort.upload(directory, image);
    }

    @Override
    public void deleteImage(String imageUrl) {
        imageStoragePort.delete(imageUrl);
    }
}
