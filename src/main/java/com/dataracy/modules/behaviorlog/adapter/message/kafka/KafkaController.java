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

    @GetMapping("/moniter/kafka")
    public ResponseEntity<Void> sendLog() {
        producerTest.sendTestLog();
        return ResponseEntity.ok().build();
    }
}
