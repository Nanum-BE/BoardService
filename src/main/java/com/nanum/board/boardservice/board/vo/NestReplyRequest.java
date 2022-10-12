package com.nanum.board.boardservice.board.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestReplyRequest {

    @NotNull(message = "replyId cannot be null")
    @Schema(description = "대댓글을 달려는 댓글 Id")
    private Long replyId;

    @NotNull(message = "userId cannot be null")
    @Schema(description = "대댓글을 작성하는 사용자 Id")
    private Long userId;

    @NotNull(message = "content cannot be null")
    @Schema(description = "댓글 내용")
    private String content;
}
