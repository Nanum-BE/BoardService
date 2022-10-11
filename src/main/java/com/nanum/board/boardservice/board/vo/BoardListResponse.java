package com.nanum.board.boardservice.board.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardListResponse {
    
    @Schema(description = "게시글 id")
    private Long id;
    
    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 조회 수", defaultValue = "0")
    private Long viewCount;

    @Schema(description = "게시글의 카테고리 id")
    private Long categoryId;
    
    @Schema(description = "게시글 생성일자")
    private LocalDateTime createAt;

    
}
