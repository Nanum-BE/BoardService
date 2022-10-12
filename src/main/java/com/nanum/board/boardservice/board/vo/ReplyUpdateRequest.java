package com.nanum.board.boardservice.board.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyUpdateRequest {

    private Long replyId;

    private Long userId;

    private String content;
}
