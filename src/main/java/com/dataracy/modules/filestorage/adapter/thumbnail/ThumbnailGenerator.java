package com.dataracy.modules.filestorage.adapter.thumbnail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;

import net.coobird.thumbnailator.Thumbnails;

@Component
public class ThumbnailGenerator {
  /**
   * 업로드된 이미지 파일로부터 지정된 크기의 썸네일 이미지를 생성하여 반환합니다.
   *
   * @param originalImage 썸네일로 변환할 원본 이미지 파일
   * @param width 생성할 썸네일의 너비(픽셀 단위)
   * @param height 생성할 썸네일의 높이(픽셀 단위)
   * @return 생성된 썸네일 이미지 데이터가 담긴 ByteArrayOutputStream
   * @throws IllegalArgumentException 원본 이미지가 없거나 비어 있거나, 너비 또는 높이가 0 이하인 경우 발생합니다.
   * @throws RuntimeException 썸네일 생성 과정에서 입출력 오류가 발생한 경우 발생합니다.
   */
  public ByteArrayOutputStream createThumbnail(MultipartFile originalImage, int width, int height) {
    if (originalImage == null || originalImage.isEmpty()) {
      LoggerFactory.common().logWarning("원본 이미지 누락", "원본 이미지가 필요합니다.");
      throw new IllegalArgumentException("원본 이미지가 필요합니다.");
    }
    if (width <= 0 || height <= 0) {
      LoggerFactory.common().logWarning("너비와 높이 범위", "너비와 높이는 양수여야 합니다.");
      throw new IllegalArgumentException("너비와 높이는 양수여야 합니다.");
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try (InputStream is = originalImage.getInputStream()) {
      Thumbnails.of(is).size(width, height).outputFormat("jpg").toOutputStream(os);
      return os;
    } catch (IOException e) {
      LoggerFactory.common().logError("썸네일 생성 실패", "썸네일 생성 중 에러가 발생하였습니다.");
      throw new CommonException(CommonErrorStatus.THUMBNAIL_GENERATION_FAILURE);
    }
  }
}
