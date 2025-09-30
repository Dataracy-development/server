package com.dataracy.modules.common.util;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtil {
    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private FileUtil() {
    }

    /**
     * 파일의 크기가 지정된 최대 크기를 초과하는지 검사하고, 초과 시 예외를 발생시킵니다.
     *
     * @param file 검사할 파일
     * @param maxSize 허용되는 최대 파일 크기(바이트 단위)
     * @param errorStatus 파일 크기 초과 시 사용할 에러 상태
     * @throws CommonException 파일 크기가 최대 허용치를 초과할 경우 발생
     */
    private static void checkFileSize(MultipartFile file, long maxSize, CommonErrorStatus errorStatus) {
        if (file.getSize() > maxSize) {
            LoggerFactory.common().logError("파일 용량 검증", errorStatus.getMessage());
            throw new CommonException(errorStatus);
        }
    }

    /**
     * 파일의 확장자가 지정된 정규식 패턴과 일치하지 않을 경우 예외를 발생시킵니다.
     *
     * @param originalFilename 검증할 파일명
     * @param regex 허용되는 확장자 정규식 패턴
     * @param errorStatus 일치하지 않을 때 발생시킬 예외 상태
     * @throws CommonException 파일명이 null이거나 파일 확장자가 허용되지 않은 경우
     */
    private static void checkFileType(String originalFilename, String regex, CommonErrorStatus errorStatus) {
        if (originalFilename == null || !originalFilename.matches(regex)) {
            LoggerFactory.common().logError("파일 형식 검증", errorStatus.getMessage());
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
        long maxSize = 5L * 1024 * 1024; // 5MB
        checkFileSize(file, maxSize, CommonErrorStatus.OVER_MAXIMUM_IMAGE_FILE_SIZE);
        checkFileType(file.getOriginalFilename(), "(?i).+\\.(jpg|jpeg|png)$", CommonErrorStatus.BAD_REQUEST_IMAGE_FILE_TYPE);
    }

    /**
     * 일반 파일 업로드 시 파일의 크기와 확장자를 검증합니다.
     * 파일이 null이거나 비어 있으면 검증을 건너뜁니다. 파일 크기가 100MB를 초과하거나 확장자가 xlsx, csv, json이 아닌 경우 CommonException이 발생합니다.
     *
     * @param file 업로드할 파일
     * @throws CommonException 파일 크기가 100MB를 초과하거나 허용되지 않은 확장자인 경우 발생
     */
    public static void validateGeneralFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        long maxSize = 200L * 1024 * 1024; // 200MB (데이터 분석 커뮤니티 수준)
        checkFileSize(file, maxSize, CommonErrorStatus.OVER_MAXIMUM_FILE_SIZE);
        checkFileType(file.getOriginalFilename(), "(?i).+\\.(xlsx|csv|json)$", CommonErrorStatus.BAD_REQUEST_FILE_TYPE);
    }
}
