package com.dataracy.modules.filestorage.support.util;

import com.dataracy.modules.common.logging.support.LoggerFactory;

import java.util.UUID;

public final class S3KeyGeneratorUtil {
    private S3KeyGeneratorUtil() {
    }

    /**
     * S3에 저장할 파일의 키를 생성합니다.
     *
     * 입력받은 도메인, 엔티티 ID, 원본 파일명으로부터 확장자를 추출하고, 랜덤 UUID를 조합하여 `도메인/엔티티ID/UUID.확장자` 형식의 S3 키를 반환합니다.
     *
     * @param domain S3 경로의 최상위 디렉터리로 사용할 도메인 이름
     * @param entityId 파일과 연관된 엔티티의 ID
     * @param originalFilename 원본 파일명 (확장자 포함)
     * @return 생성된 S3 키 문자열
     * @throws IllegalArgumentException 입력 파라미터 중 하나라도 null인 경우
     */
    public static String generateKey(String domain, Long entityId, String originalFilename) {
        if (domain == null || entityId == null || originalFilename == null) {
            LoggerFactory.common().logWarning("s3에 저장할 파일의 키 발급", "파라미터는 null일 수 없습니다.");
            throw new IllegalArgumentException("파라미터는 null일 수 없습니다.");
        }
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%d/%s.%s", domain, entityId, uuid, extension);
    }

    /**
     * 도메인, 엔티티 ID, 원본 파일명을 기반으로 S3에 저장할 썸네일 파일의 고유 키를 생성합니다.
     *
     * 반환되는 키는 `domain/entityId/thumb/uuid.extension` 형식이며, 파일 확장자는 원본 파일명에서 추출됩니다. 입력값이 null인 경우 예외가 발생합니다.
     *
     * @param domain S3 경로에 포함될 도메인 이름
     * @param entityId S3 경로에 포함될 엔티티 ID
     * @param originalFilename 확장자 추출에 사용되는 원본 파일명
     * @return 생성된 썸네일 S3 키 문자열
     */
    public static String generateThumbnailKey(String domain, Long entityId, String originalFilename) {
        if (domain == null || entityId == null || originalFilename == null) {
            LoggerFactory.common().logWarning("썸네일 파일의 고유 키 발급", "파라미터는 null일 수 없습니다.");
            throw new IllegalArgumentException("파라미터는 null일 수 없습니다");
        }
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%d/thumb/%s.%s", domain, entityId, uuid, extension);
    }

    /**
     * 파일명에서 확장자를 추출하여 반환합니다.
     *
     * 파일명에 점(`.`)이 없거나 마지막이 점으로 끝나는 경우 기본 확장자인 "jpg"를 반환합니다.
     *
     * @param filename 확장자를 추출할 파일명
     * @return 추출된 확장자 문자열 또는 기본값 "jpg"
     */
    private static String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "jpg"; // 기본 확장자
        }
        return filename.substring(lastDot + 1);
    }
}
