package com.dataracy.modules.comment.adapter.web.mapper.command;

import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import org.springframework.stereotype.Component;

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
