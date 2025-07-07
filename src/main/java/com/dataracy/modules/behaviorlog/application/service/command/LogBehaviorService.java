//package com.dataracy.modules.behaviorlog.application.service.command;
//
//import com.dataracy.modules.behaviorlog.application.port.in.LogBehaviorUseCase;
//import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSavePort;
//import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
///**
// * Kafka나 DB로 로그를 저장하는 서비스
// */
//@Service
//@RequiredArgsConstructor
//public class LogBehaviorService implements LogBehaviorUseCase {
//    private final BehaviorLogSavePort savePort;
//
//    @Override
//    public void log(BehaviorLog log) {
//        savePort.save(log);
//    }
//}
