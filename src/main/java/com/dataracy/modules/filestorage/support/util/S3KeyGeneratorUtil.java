package com.dataracy.modules.filestorage.support.util;

import java.util.UUID;

public class S3KeyGeneratorUtil {

    public static String generateKey(String domain, Long entityId, String originalFilename) {
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
