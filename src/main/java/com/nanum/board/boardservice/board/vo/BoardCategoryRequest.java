package com.nanum.board.boardservice.board.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BoardCategoryRequest {

    @NotNull(message = "name could not be null")
    @Schema(description = "게시판 카테고리 이름", defaultValue = "자유게시판")
    private String name;
}
