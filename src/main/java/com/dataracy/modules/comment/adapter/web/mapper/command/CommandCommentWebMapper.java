package com.dataracy.modules.comment.adapter.web.mapper.command;

import org.springframework.stereotype.Component;

import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.response.command.UploadCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.dto.response.command.UploadCommentResponse;

/** 댓글 command 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class CommandCommentWebMapper {
  /**
   * 댓글 업로드 웹 요청 객체를 애플리케이션 계층의 DTO로 변환합니다.
   *
   * @param webRequest 업로드할 댓글의 내용과 부모 댓글 ID를 포함한 웹 요청 객체
   * @return 댓글 업로드 요청을 나타내는 DTO
   */
  public UploadCommentRequest toApplicationDto(UploadCommentWebRequest webRequest) {
    return new UploadCommentRequest(webRequest.content(), webRequest.parentCommentId());
  }

  /**
   * 애플리케이션 계층의 댓글 업로드 응답을 웹 계층 DTO로 변환한다.
   *
   * @param responseDto 업로드된 댓글의 식별자(id)를 포함한 애플리케이션 응답 DTO
   * @return 웹 계층에서 사용될 UploadCommentWebResponse (생성된 댓글의 id를 포함)
   */
  public UploadCommentWebResponse toWebDto(UploadCommentResponse responseDto) {
    return new UploadCommentWebResponse(responseDto.id());
  }

  /**
   * 댓글 수정 웹 요청 객체를 애플리케이션 계층의 댓글 수정 요청 DTO로 변환합니다.
   *
   * @param webRequest 수정할 댓글의 내용을 포함한 웹 요청 객체
   * @return 댓글 수정 요청을 나타내는 DTO
   */
  public ModifyCommentRequest toApplicationDto(ModifyCommentWebRequest webRequest) {
    return new ModifyCommentRequest(webRequest.content());
  }
}
