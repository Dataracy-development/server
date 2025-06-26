package com.dataracy.common.util;

import com.dataracy.common.status.CommonErrorStatus;
import com.dataracy.common.status.CommonException;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    private FileUtil() {
        throw new CommonException(CommonErrorStatus.CAN_NOT_INSTANTIATE_FILE_UTILITY_CLASS);
    }

    public static void validateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new CommonException(CommonErrorStatus.OVER_MAXIMUM_IMAGE_FILE_SIZE);
        }

        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.matches("(?i).+\\.(jpg|jpeg|png)$")) {
            throw new CommonException(CommonErrorStatus.BAD_REQUEST_IMAGE_FILE_TYPE);
        }
    }

    public static void validateGeneralFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.getSize() > maxSize) {
            throw new CommonException(CommonErrorStatus.OVER_MAXIMUM_FILE_SIZE);
        }

        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.matches("(?i).+\\.(jpg|jpeg|png|pdf|docx|xlsx|pptx|txt)$")) {
            throw new CommonException(CommonErrorStatus.BAD_REQUEST_FILE_TYPE);
        }
    }
}
