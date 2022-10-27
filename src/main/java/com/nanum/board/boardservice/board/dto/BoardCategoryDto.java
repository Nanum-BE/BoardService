package com.nanum.board.boardservice.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BoardCategoryDto {
    @Schema(description = "게시글 id")
    private Long id;

    @Schema(description = "게시글 제목")
    private String title;

    private String content;
    private Long userId;

    @Schema(description = "게시글 조회 수", defaultValue = "0")
    private Long viewCount;

    @Schema(description = "게시글 생성일자")
    private LocalDateTime createAt;


    @Schema(description = "게시글의 카테고리 id")
    private Long categoryId;

    private String categoryName;

    @QueryProjection
    public BoardCategoryDto(Long id, String title, String content, Long userId, Long viewCount, LocalDateTime createAt, Long categoryId, String categoryName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.viewCount = viewCount;
        this.createAt = createAt;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
