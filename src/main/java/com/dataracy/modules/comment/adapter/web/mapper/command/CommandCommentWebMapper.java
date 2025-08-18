package com.dataracy.modules.comment.adapter.web.mapper.command;

import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.response.command.UploadCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.dto.response.command.UploadCommentResponse;
import org.springframework.stereotype.Component;

/**
 * 댓글 command 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class CommandCommentWebMapper {
    /**
     * 댓글 업로드 웹 요청 객체를 애플리케이션 계층의 DTO로 변환합니다.
     *
     * @param webRequest 업로드할 댓글의 내용과 부모 댓글 ID를 포함한 웹 요청 객체
     * @return 댓글 업로드 요청을 나타내는 DTO
     */
    public UploadCommentRequest toApplicationDto(UploadCommentWebRequest webRequest) {
        return new UploadCommentRequest(
                webRequest.content(),
                webRequest.parentCommentId()
        );
    }

    /**
     * 댓글 업로드 웹 응답 DTO를 반환한다.
     *
     * @param responseDto 댓글 업로드 애플리케이션 응답 DTO
     * @return 업로드 한 댓글 아이디
     */
    public UploadCommentWebResponse toWebDto(UploadCommentResponse responseDto) {
        return new UploadCommentWebResponse(
                responseDto.id()
        );
    }

    /**
     * 댓글 수정 웹 요청 객체를 애플리케이션 계층의 댓글 수정 요청 DTO로 변환합니다.
     *
     * @param webRequest 수정할 댓글의 내용을 포함한 웹 요청 객체
     * @return 댓글 수정 요청을 나타내는 DTO
     */
    public ModifyCommentRequest toApplicationDto(ModifyCommentWebRequest webRequest) {
        return new ModifyCommentRequest(
                webRequest.content()
        );
    }
}
