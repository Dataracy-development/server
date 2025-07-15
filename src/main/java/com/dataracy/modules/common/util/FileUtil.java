package com.dataracy.modules.common.util;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtil {
    private FileUtil() {
    }

    /**
     * 파일의 크기가 일정 크기를 넘어갈 경우 예외
     * @param file 파일
     * @param maxSize 일정 크기
     * @param errorStatus 예외
     */
    private static void checkFileSize(MultipartFile file, long maxSize, CommonErrorStatus errorStatus) {
        if (file.getSize() > maxSize) {
            throw new CommonException(errorStatus);
        }
    }

    /**
     * 파일 타입이 특정 타입에 맞지 않을 경우
     * @param originalFilename 파일명
     * @param regex 특정 확장자 형식
     * @param errorStatus 예외
     */
    private static void checkFileType(String originalFilename, String regex, CommonErrorStatus errorStatus) {
        if (!originalFilename.matches(regex)) {
            throw new CommonException(errorStatus);
        }
    }

    /**
     * 이미지 파일의 유효성을 검사합니다.
     *
     * 파일이 null이거나 비어 있으면 검사를 수행하지 않습니다.  
     * 파일이 5MB를 초과하거나 확장자가 jpg, jpeg, png(대소문자 무관)가 아니면 예외를 발생시킵니다.
     *
     * @param file 검사할 이미지 파일
     * @throws CommonException 파일 크기 또는 확장자가 허용되지 않을 경우 발생합니다.
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        long maxSize = 5 * 1024 * 1024; // 5MB
        checkFileSize(file, maxSize, CommonErrorStatus.OVER_MAXIMUM_IMAGE_FILE_SIZE);
        checkFileType(file.getOriginalFilename(), "(?i).+\\.(jpg|jpeg|png)$", CommonErrorStatus.BAD_REQUEST_IMAGE_FILE_TYPE);
    }

    /**
     * 일반 파일 일 경우 조건
     */
    public static void validateGeneralFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        long maxSize = 50 * 1024 * 1024; // 50MB
        checkFileSize(file, maxSize, CommonErrorStatus.OVER_MAXIMUM_FILE_SIZE);
        checkFileType(file.getOriginalFilename(), "(?i).+\\.(xlsx|csv|json)$", CommonErrorStatus.BAD_REQUEST_FILE_TYPE);
    }
}
