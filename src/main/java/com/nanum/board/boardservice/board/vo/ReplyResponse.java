package com.nanum.board.boardservice.board.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResponse {

    @Schema(description = "상위 댓글 Id")
    private Long replyId;

    @Schema(description = "댓글을 작성한 사용자 별명")
    private String nickName;

    private Long userId;

    @Schema(description = "댓글을 작성한 사용자 프로필 이미지")
    private String imgUrl;

    @Schema(description = "댓글 하위에 달려있는 대댓글의 개수")
    private Long nestedCount;

    private String content;

    private LocalDateTime createAt;

    private Long id;
}
