/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.filestorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/** 파일 스토리지 관련 설정값들을 외부화 */
@Component
@ConfigurationProperties(prefix = "file-storage")
@Getter
@Setter
public class FileStorageProperties {

  /** 파일 크기별 처리 방식 설정 */
  private FileSize fileSize = new FileSize();

  /** 멀티파트 업로드 설정 */
  private Multipart multipart = new Multipart();

  /** 버퍼 설정 */
  private Buffer buffer = new Buffer();

  // SpotBugs EI_EXPOSE_REP 경고 해결을 위한 방어적 복사
  // ConfigurationProperties는 Spring이 관리하지만, 외부에서 직접 접근 시 안전성 보장
  public FileSize getFileSize() {
    FileSize copy = new FileSize();
    copy.setMultipartThreshold(this.fileSize.getMultipartThreshold());
    copy.setStreamingThreshold(this.fileSize.getStreamingThreshold());
    return copy;
  }

  public Multipart getMultipart() {
    Multipart copy = new Multipart();
    copy.setChunkSize(this.multipart.getChunkSize());
    copy.setMaxParts(this.multipart.getMaxParts());
    return copy;
  }

  public Buffer getBuffer() {
    Buffer copy = new Buffer();
    copy.setDefaultSize(this.buffer.getDefaultSize());
    copy.setStreamingSize(this.buffer.getStreamingSize());
    return copy;
  }

  @Getter
  @Setter
  public static class FileSize {
    /** 멀티파트 업로드 적용 기준 (바이트) */
    private long multipartThreshold = 20L * 1024 * 1024; // 20MB

    /** 스트리밍 업로드 적용 기준 (바이트) */
    private long streamingThreshold = 5L * 1024 * 1024; // 5MB

    // primitive 타입만 사용하므로 방어적 복사 불필요
  }

  @Getter
  @Setter
  public static class Multipart {
    /** 멀티파트 청크 크기 (바이트) */
    private long chunkSize = 5L * 1024 * 1024; // 5MB

    /** 최대 파트 수 */
    private int maxParts = 10000;

    // primitive 타입만 사용하므로 방어적 복사 불필요
  }

  @Getter
  @Setter
  public static class Buffer {
    /** 기본 버퍼 크기 (바이트) */
    private int defaultSize = 8192; // 8KB

    /** 스트리밍 버퍼 크기 (바이트) */
    private int streamingSize = 16384; // 16KB

    // primitive 타입만 사용하므로 방어적 복사 불필요
  }
}
