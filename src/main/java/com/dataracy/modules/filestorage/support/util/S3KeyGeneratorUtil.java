package com.dataracy.modules.filestorage.support.util;

import java.util.UUID;

public final class S3KeyGeneratorUtil {

    public static String generateKey(String domain, Long entityId, String originalFilename) {
        if (domain == null || entityId == null || originalFilename == null) {
            throw new IllegalArgumentException("파라미터는 null일 수 없습니다");
        }
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%d/%s.%s", domain, entityId, uuid, extension);
    }

    public static String generateThumbnailKey(String domain, Long entityId, String originalFilename) {
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%d/thumb/%s.%s", domain, entityId, uuid, extension);
    }

    private static String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "jpg"; // 기본 확장자
        }
        return filename.substring(lastDot + 1);
    }
}
