package com.nanum.board.boardservice.board.dto;

import com.nanum.board.boardservice.board.domain.Board;
import com.nanum.board.boardservice.board.domain.BoardCategory;
import com.nanum.board.boardservice.board.infrastructure.BoardCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BoardDto {

    private Long userId;
    private String title;
    private String content;
    private Long boardCategoryId;
}
