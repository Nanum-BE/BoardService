package com.nanum.board.boardservice.board.dto;

import com.nanum.board.boardservice.board.domain.BoardCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BoardUserDto {
    private Long id;
    private Long userId;
    private Long viewCount;
    private String title;
    private String content;
    private Long categoryId;
    private LocalDateTime createAt;
}
