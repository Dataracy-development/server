/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.elasticsearch.document;

public record ProjectDeletedUpdate(Boolean isDeleted) {
  /**
   * 프로젝트가 삭제된 상태를 나타내는 {@code ProjectDeletedUpdate} 인스턴스를 반환합니다.
   *
   * @return 삭제 상태로 표시된 {@code ProjectDeletedUpdate} 객체
   */
  public static ProjectDeletedUpdate deleted() {
    return new ProjectDeletedUpdate(true);
  }

  /**
   * 프로젝트가 복구되었음을 나타내는 ProjectDeletedUpdate 인스턴스를 반환합니다.
   *
   * @return 삭제 상태가 false로 설정된 ProjectDeletedUpdate 인스턴스
   */
  public static ProjectDeletedUpdate restored() {
    return new ProjectDeletedUpdate(false);
  }
}
