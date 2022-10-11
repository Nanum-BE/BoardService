package com.nanum.board.boardservice.board.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class BoardRequest {

    @NotNull(message = "userId cannot be null")
    @Schema(description = "사용자 식별 id")
    private Long userId;

    @NotNull(message = "title cannot be null")
    @Schema(description = "게시글 제목")
    @Size(min = 2, message = "title not be less than two characters")
    private String title;

    @NotNull(message = "content cannot be null")
    @Schema(description = "게시글 내용")
    @Size(min = 2, message = "content not be less than two characters")
    private String content;

    @NotNull(message = "categoryId cannot be null")
    @Schema(description = "게시글 카테고리 id")
    private Long boardCategoryId;
}
