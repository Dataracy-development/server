package com.dataracy;

import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;
import com.dataracy.modules.user.adapter.web.api.validate.UserValidateController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
public class LockTest {

    private MockMvc mockMvc;

    @Autowired
    private UserValidateController userController; // ✅ 실제 컨트롤러 (nickname 체크가 있는 컨트롤러)

    @Autowired
    private RedissonClient redissonClient; // ✅ 실 사용 Redisson 클라이언트

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // ✅ Spring Security 없이 컨트롤러만 등록
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testNicknameLock_concurrentAccess() throws Exception {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // ✅ 테스트용 닉네임 DTO JSON 생성
        String json = objectMapper.writeValueAsString(new DuplicateNicknameRequest("주니22"));

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(
                            post("/api/v1/public/nickname/check") // ✅ 실제 API 경로
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
                    ).andDo(print());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 종료까지 대기
        executor.shutdown();
    }
}
