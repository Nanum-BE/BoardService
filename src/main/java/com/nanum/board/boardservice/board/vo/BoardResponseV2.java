package com.nanum.board.boardservice.board.vo;

import com.nanum.board.boardservice.board.domain.Board;
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
public class BoardResponseV2 {


    private Long userId;


    private Long recommendId;

    private String nickName;
    List<BoardImgResponse> imgUrls;
    private Board board;
}
