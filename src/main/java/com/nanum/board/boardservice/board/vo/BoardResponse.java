package com.nanum.board.boardservice.board.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long categoryId;
    private String nickName;
    private String profileImgUrl;
    private Long recommendId;
    private Long viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    List<BoardImgResponse> imgUrls;

}
