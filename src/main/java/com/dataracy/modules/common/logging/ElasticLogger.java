package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class ElasticLogger extends BaseLogger {
  /**
   * Elasticsearch 쿼리의 시작을 로그로 기록하고 현재 시각을 반환합니다.
   *
   * @param index 조회 대상 Elasticsearch 인덱스 이름
   * @param message 추가 메시지 또는 설명
   * @return 쿼리 시작 시점의 Instant
   */
  public Instant logQueryStart(String index, String message) {
    debug("[Elasticsearch 조회 시작] index={} message={}", index, message);
    return Instant.now();
  }

  /**
   * Elasticsearch 쿼리의 완료를 로그로 기록하며, 실행 시간을 밀리초 단위로 포함합니다.
   *
   * @param index 조회가 수행된 Elasticsearch 인덱스 이름
   * @param message 추가 메시지 또는 설명
   * @param startTime 쿼리 시작 시각
   */
  public void logQueryEnd(String index, String message, Instant startTime) {
    long durationMs = Duration.between(startTime, Instant.now()).toMillis();
    debug("[Elasticsearch 조회 완료] index={} message={} duration={}ms", index, message, durationMs);
  }

  /**
   * Elasticsearch 인덱스에 문서를 추가하는 작업을 정보 레벨로 기록합니다.
   *
   * @param index 인덱싱 대상 Elasticsearch 인덱스 이름
   * @param docId 인덱싱되는 문서의 ID
   * @param message 추가적인 설명 또는 메시지
   */
  public void logIndex(String index, String docId, String message) {
    info("[Elasticsearch 인덱싱] index={} docId={} message={}", index, docId, message);
  }

  /**
   * 지정된 인덱스와 문서 ID에 대한 Elasticsearch 업데이트 작업을 정보 수준으로 로그에 기록합니다.
   *
   * @param index 업데이트가 수행되는 Elasticsearch 인덱스 이름
   * @param docId 업데이트되는 문서의 ID
   * @param message 추가적인 설명 또는 메시지
   */
  public void logUpdate(String index, String docId, String message) {
    info("[Elasticsearch 업데이트] index={} docId={} message={}", index, docId, message);
  }

  /**
   * Elasticsearch에서 수행된 검색 작업을 인덱스, 쿼리, 메시지와 함께 디버그 레벨로 기록합니다.
   *
   * @param index 검색이 수행된 Elasticsearch 인덱스 이름
   * @param query 실행된 검색 쿼리 문자열
   * @param message 추가적인 설명 또는 메시지
   */
  public void logSearch(String index, String query, String message) {
    debug("[Elasticsearch 검색] index={} query={} message={}", index, query, message);
  }

  /**
   * Elasticsearch 작업 중 발생한 예외를 인덱스와 메시지와 함께 에러 레벨로 기록합니다.
   *
   * @param index 오류가 발생한 Elasticsearch 인덱스 이름
   * @param message 오류에 대한 설명 메시지
   * @param e 기록할 예외 객체
   */
  public void logError(String index, String message, Throwable e) {
    error(e, "[Elasticsearch 오류] index={} message={}", index, message);
  }
}
