package com.dataracy.modules.dataset.application.service.command;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateMetadataParsingStatusPort;
import com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort;
import com.dataracy.modules.dataset.domain.enums.MetadataParsingStatus;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;

import lombok.RequiredArgsConstructor;

/**
 * 데이터셋 메타데이터 파싱 관련 알림을 처리하는 서비스
 *
 * <p>파싱 완료/실패 시 사용자에게 이메일 알림을 전송합니다.
 */
@Service
@RequiredArgsConstructor
public class DataParsingNotificationService {
  private final FindDataPort findDataPort;
  private final UserQueryPort userQueryPort;
  private final SendEmailPort sendEmailPort;
  private final UpdateMetadataParsingStatusPort updateMetadataParsingStatusPort;

  private static final String NOTIFICATION_SERVICE = "DataParsingNotificationService";

  /**
   * 파싱 성공 시 사용자에게 이메일을 전송합니다.
   *
   * @param userId 사용자 ID
   * @param dataTitle 데이터셋 제목
   * @param dataId 데이터셋 ID
   */
  public void notifyParsingSuccess(Long userId, String dataTitle, Long dataId) {
    try {
      userQueryPort
          .findUserById(userId)
          .ifPresent(
              user -> {
                String email = user.getEmail();
                if (email != null && !email.isBlank()) {
                  String title = "[Dataracy] 데이터셋 메타데이터 파싱 완료";
                  String body =
                      String.format(
                          "안녕하세요.\n\n"
                              + "업로드하신 데이터셋 '%s'의 메타데이터 파싱이 완료되었습니다.\n\n"
                              + "데이터셋 ID: %d\n\n"
                              + "이제 데이터셋을 검색하고 활용할 수 있습니다.\n\n"
                              + "감사합니다.",
                          dataTitle, dataId);
                  sendEmailPort.send(email, title, body);
                  LoggerFactory.service()
                      .logInfo(
                          NOTIFICATION_SERVICE,
                          "파싱 성공 이메일 전송 완료 - userId=" + userId + ", dataId=" + dataId);
                }
              });
    } catch (Exception e) {
      // 이메일 전송 실패는 로그만 남기고 파싱 성공 자체는 유지
      LoggerFactory.service()
          .logException(
              NOTIFICATION_SERVICE, "파싱 성공 이메일 전송 실패 - userId=" + userId + ", dataId=" + dataId, e);
    }
  }

  /**
   * 파싱 실패 시 사용자에게 이메일을 전송합니다.
   *
   * @param dataId 데이터셋 ID
   * @param errorMessage 오류 메시지
   */
  public void notifyParsingFailure(Long dataId, String errorMessage) {
    try {
      Optional<Data> dataOpt = findDataPort.findDataById(dataId);
      if (dataOpt.isEmpty()) {
        LoggerFactory.service()
            .logWarning(NOTIFICATION_SERVICE, "파싱 실패 이메일 전송 실패 - 데이터셋을 찾을 수 없음 dataId=" + dataId);
        return;
      }

      Data data = dataOpt.get();
      userQueryPort
          .findUserById(data.getUserId())
          .ifPresent(
              user -> {
                String email = user.getEmail();
                if (email != null && !email.isBlank()) {
                  String title = "[Dataracy] 데이터셋 메타데이터 파싱 실패";
                  String body =
                      String.format(
                          "안녕하세요.\n\n"
                              + "업로드하신 데이터셋 '%s'의 메타데이터 파싱 중 오류가 발생했습니다.\n\n"
                              + "데이터셋 ID: %d\n"
                              + "오류 내용: %s\n\n"
                              + "현재 데이터셋 파싱 재시도가 진행중입니다."
                              + "파일 형식이나 내용을 확인해주시고, 재시도 최종 실패 시 다시 연락드리겠습니다.\n\n"
                              + "감사합니다.",
                          data.getTitle(), dataId, errorMessage);
                  sendEmailPort.send(email, title, body);
                  LoggerFactory.service()
                      .logInfo(
                          NOTIFICATION_SERVICE,
                          "파싱 실패 이메일 전송 완료 - userId=" + data.getUserId() + ", dataId=" + dataId);
                }
              });
    } catch (Exception e) {
      // 이메일 전송 실패는 로그만 남김
      LoggerFactory.service()
          .logException(NOTIFICATION_SERVICE, "파싱 실패 이메일 전송 실패 - dataId=" + dataId, e);
    }
  }

  /**
   * 모든 재시도 실패 후 완전 실패 시 사용자에게 이메일을 전송하고 파싱 상태를 업데이트합니다.
   *
   * @param dataId 데이터셋 ID
   */
  public void notifyFinalFailure(Long dataId) {
    try {
      Optional<Data> dataOpt = findDataPort.findDataById(dataId);
      if (dataOpt.isEmpty()) {
        LoggerFactory.service()
            .logWarning(NOTIFICATION_SERVICE, "완전 실패 이메일 전송 실패 - 데이터셋을 찾을 수 없음 dataId=" + dataId);
        return;
      }

      Data data = dataOpt.get();

      // 파싱 상태를 FAILED로 업데이트
      updateMetadataParsingStatusPort.updateParsingStatus(dataId, MetadataParsingStatus.FAILED);

      // 완전 실패 이메일 전송
      userQueryPort
          .findUserById(data.getUserId())
          .ifPresent(
              user -> {
                String email = user.getEmail();
                if (email != null && !email.isBlank()) {
                  String title = "[Dataracy] 데이터셋 메타데이터 파싱 완전 실패";
                  String body =
                      String.format(
                          "안녕하세요.\n\n"
                              + "업로드하신 데이터셋 '%s'의 메타데이터 파싱이 모든 재시도 후에도 실패했습니다.\n\n"
                              + "데이터셋 ID: %d\n\n"
                              + "시스템이 여러 번 시도했으나 파싱에 실패했습니다.\n"
                              + "파일 형식이나 내용을 확인해주시고, 문제가 지속되면 고객지원으로 문의해주세요.\n\n"
                              + "감사합니다.",
                          data.getTitle(), dataId);
                  sendEmailPort.send(email, title, body);
                  LoggerFactory.service()
                      .logInfo(
                          NOTIFICATION_SERVICE,
                          "완전 실패 이메일 전송 완료 - userId=" + data.getUserId() + ", dataId=" + dataId);
                }
              });
    } catch (Exception e) {
      // 이메일 전송 실패는 로그만 남김
      LoggerFactory.service()
          .logException(NOTIFICATION_SERVICE, "완전 실패 이메일 전송 실패 - dataId=" + dataId, e);
    }
  }
}
