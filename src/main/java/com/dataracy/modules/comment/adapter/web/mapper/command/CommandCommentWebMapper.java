package com.dataracy.modules.comment.adapter.web.mapper.command;

import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import org.springframework.stereotype.Component;

@Component
public class CommandCommentWebMapper {
    /**
     * 웹 요청 객체를 애플리케이션 계층의 댓글 업로드 요청 DTO로 변환합니다.
     *
     * @param webRequest 댓글 업로드 웹 요청 객체
     * @return 변환된 댓글 업로드 요청 DTO
     */
    public UploadCommentRequest toApplicationDto(UploadCommentWebRequest webRequest) {
        return new UploadCommentRequest(
                webRequest.content(),
                webRequest.parentCommentId()
        );
    }

    /**
     * 웹 요청 객체를 애플리케이션 계층의 댓글 수정 요청 DTO로 변환합니다.
     *
     * @param webRequest 댓글 수정 웹 요청 객체
     * @return 댓글 수정 요청 DTO
     */
    public ModifyCommentRequest toApplicationDto(ModifyCommentWebRequest webRequest) {
        return new ModifyCommentRequest(
                webRequest.content()
        );
    }
}
