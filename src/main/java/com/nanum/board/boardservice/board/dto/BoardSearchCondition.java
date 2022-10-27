package com.nanum.board.boardservice.board.dto;

import com.nanum.board.boardservice.board.domain.BoardCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardSearchCondition {
    private String title;
    private String content;
    private Long categoryId;
    private String all;
}
