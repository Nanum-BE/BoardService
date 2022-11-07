package com.nanum.board.boardservice.board.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardTotalResponse {
    @Schema(description = "게시글 개수")
    private Long postCount;

    @Schema(description = "댓글 개수")
    private Long replyCount;
}
