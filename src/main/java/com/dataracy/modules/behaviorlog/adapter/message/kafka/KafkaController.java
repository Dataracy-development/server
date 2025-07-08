package com.dataracy.modules.behaviorlog.adapter.message.kafka;

import com.dataracy.modules.behaviorlog.adapter.message.kafka.test.BehaviorLogProducerTest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaController {

    private final BehaviorLogProducerTest producerTest;

    /**
     * `/moniter/kafka` 엔드포인트에 대한 GET 요청을 처리하여 테스트 로그를 카프카로 전송합니다.
     *
     * @return 본문이 없는 HTTP 200 OK 응답
     */
    @GetMapping("/moniter/kafka")
    public ResponseEntity<Void> sendLog() {
        producerTest.sendTestLog();
        return ResponseEntity.ok().build();
    }
}
