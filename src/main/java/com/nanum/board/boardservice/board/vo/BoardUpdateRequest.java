package com.nanum.board.boardservice.board.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardUpdateRequest {

    @NotNull(message = "boardId cannot be null")
    @Schema(description = "수정할 게시글 id", defaultValue = "1")
    private Long boardId;

    @NotNull(message = "userId cannot be null")
    @Schema(description = "게시글을 수정하는 사용자 id", defaultValue = "1")
    private Long userId;

    @NotNull(message = "categoryId cannot be null")
    @Schema(description = "카테고리를 변경한다면 변경하고자 하는 카테고리 id", defaultValue = "2")
    private Long categoryId;

    @NotNull(message = "title cannot be null")
    @Schema(description = "입력한 내용으로 게시글의 제목을 수정", defaultValue = "수정해서 올려요")
    private String title;

    @NotNull(message = "content cannot be null")
    @Schema(description = "입력한 내용으로 게시글의 내용을 수정", defaultValue = "내용도 좀 수정할게요")
    private String content;

    @Schema(description = "기존의 이미지에서 지우고자 하는 이미지 id", defaultValue = "1,2")
    List<Long> imgId;


}
