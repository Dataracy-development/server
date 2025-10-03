/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.email.application.port.out.command;

public interface SendEmailPort {
  /**
   * 지정된 이메일 주소로 제목과 본문을 포함한 이메일을 전송합니다.
   *
   * @param email 수신자의 이메일 주소
   * @param title 이메일 제목
   * @param body 이메일 본문 내용
   */
  void send(String email, String title, String body);
}
