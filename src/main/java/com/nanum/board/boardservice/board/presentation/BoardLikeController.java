package com.nanum.board.boardservice.board.presentation;

import com.nanum.board.boardservice.board.application.BoardService;
import com.nanum.board.config.BaseResponse;
import com.nanum.board.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시글 좋아요", description = "게시글 좋아요에 관한 API")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "201", description = "created successfully",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "204", description = "success, but no content",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "bad request",
                content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "409", description = "conflict",
                content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "500", description = "server error",
                content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
})
public class BoardLikeController {
    private final BoardService boardService;

    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요 api")
    @PostMapping("/like/posts/{postId}")
    public ResponseEntity<Object> likePosts(@PathVariable Long postId) {

        Long likeId = boardService.likePosts(postId);

        HashMap<Object, Object> map = new HashMap<>();

        map.put("recommendId", likeId);
        BaseResponse<HashMap<Object, Object>> baseResponse = new BaseResponse<>(map);
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @Operation(summary = "게시글 좋아요 취소", description = "좋아요 취소를 위한 api")
    @DeleteMapping("like/posts/{recommendId}")
    public ResponseEntity<Object> deletePosts(@PathVariable Long recommendId) {
        boardService.deleteLike(recommendId);
        String result = "좋아요 취소";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);

        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
