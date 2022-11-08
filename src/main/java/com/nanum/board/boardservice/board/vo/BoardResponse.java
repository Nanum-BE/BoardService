package com.nanum.board.boardservice.board.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
    private LocalDateTime updateAt;
    private LocalDateTime createAt;
    List<BoardImgResponse> imgUrls;

}
