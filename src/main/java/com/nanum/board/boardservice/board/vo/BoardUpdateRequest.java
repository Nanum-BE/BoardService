package com.nanum.board.boardservice.board.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardUpdateRequest {

    private Long boardId;

    private Long userId;

    private Long categoryId;

    private String title;

    private String content;

    List<Long> imgId;


}
