package com.dataracy.modules.common.util;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtil {

    private FileUtil() {
        throw new CommonException(CommonErrorStatus.CAN_NOT_INSTANTIATE_FILE_UTILITY_CLASS);
    }

    private static void checkFileSize(MultipartFile file, long maxSize, CommonErrorStatus errorStatus) {
        if (file.getSize() > maxSize) {
            throw new CommonException(errorStatus);
        }
    }

    private static void checkFileType(String originalFilename, String regex, CommonErrorStatus errorStatus) {
        if (!originalFilename.matches(regex)) {
            throw new CommonException(errorStatus);
        }
    }

    public static void validateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        long maxSize = 10 * 1024 * 1024; // 10MB
        checkFileSize(file, maxSize, CommonErrorStatus.OVER_MAXIMUM_IMAGE_FILE_SIZE);
        checkFileType(file.getOriginalFilename(), "(?i).+\\.(jpg|jpeg|png)$", CommonErrorStatus.BAD_REQUEST_IMAGE_FILE_TYPE);
    }

    public static void validateGeneralFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        long maxSize = 50 * 1024 * 1024; // 50MB
        checkFileSize(file, maxSize, CommonErrorStatus.OVER_MAXIMUM_FILE_SIZE);
        checkFileType(file.getOriginalFilename(), "(?i).+\\.(jpg|jpeg|png|pdf|docx|xlsx|pptx|txt|csv|json)$", CommonErrorStatus.BAD_REQUEST_FILE_TYPE);
    }
}
