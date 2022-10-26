package com.nanum.board.boardservice.board.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private String nickName;
    private String profileImgUrl;
    private Long recommendId;
    private Long viewCount;
    private LocalDateTime updateAt;
    private LocalDateTime creatAt;
    List<BoardImgResponse> imgUrls;

}
