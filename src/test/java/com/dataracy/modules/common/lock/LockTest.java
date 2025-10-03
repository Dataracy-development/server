/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.lock;

import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dataracy.modules.user.adapter.web.api.validate.UserValidateController;
import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class LockTest {

  private MockMvc mockMvc;

  @Autowired private UserValidateController userController; // 실제 컨트롤러 (nickname 체크가 있는 컨트롤러)

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    // Spring Security 없이 컨트롤러만 등록
    this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  void testNicknameLock_concurrentAccess() throws Exception {
    // given
    int threadCount = 5;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    // 테스트용 닉네임 DTO JSON 생성
    String json = objectMapper.writeValueAsString(new DuplicateNicknameRequest("주니22"));

    // when
    for (int i = 0; i < threadCount; i++) {
      executor.submit(
          () -> {
            try {
              mockMvc
                  .perform(
                      post("/api/v1/public/nickname/check") // 실제 API 경로
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(json))
                  .andDo(print());
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              latch.countDown();
            }
          });
    }

    // then
    thenCode(
            () -> {
              latch.await(); // 모든 스레드 종료까지 대기
              executor.shutdown();
            })
        .doesNotThrowAnyException();
  }
}
