package com.nanum.board.boardservice.board.presentation;

import com.nanum.board.boardservice.board.application.BoardService;
import com.nanum.board.boardservice.board.vo.NestReplyRequest;
import com.nanum.board.boardservice.board.vo.ReplyRequest;
import com.nanum.board.boardservice.board.vo.ReplyResponse;
import com.nanum.board.boardservice.board.vo.ReplyUpdateRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 댓글 관련 api", description = "게시글 댓글 API")
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
public class ReplyController {

    private final BoardService boardService;

    @Operation(summary = "게시글 댓글 생성", description = "댓글을 먼저 생성하기 위한 api")
    @PostMapping("/board/reply/{userId}")
    public ResponseEntity<Object> createComment(@PathVariable Long userId,
                                                @RequestBody ReplyRequest replyRequest) {
        boardService.createComment(userId, replyRequest);

        String result = "댓글 생성이 완료되었습니다";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @Operation(summary = "상세 게시글 댓글 조회", description = "조회할 게시글에 대한 댓글 전체 조회 api")
    @GetMapping("/board/reply/{boardId}")
    public ResponseEntity<BaseResponse<List<ReplyResponse>>> retrieveReply(@PathVariable Long boardId) {

        List<ReplyResponse> replyResponses = boardService.retrieveReply(boardId);

        BaseResponse<List<ReplyResponse>> baseResponse = new BaseResponse<>(replyResponses);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @Operation(summary = "상세 게시글 댓글 수정", description = "특정 게시물의 특정 댓글 내용 수정 api")
    @PutMapping("/board/reply")
    public ResponseEntity<Object> updateReply(@RequestBody ReplyUpdateRequest replyUpdateRequest) {
        boardService.updateReply(replyUpdateRequest);

        String result = "정상적으로 수정되었습니다";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @Operation(summary = "게시글 대댓글 생성", description = "특정 댓글에 대한 대댓글 생성")
    @PostMapping("/board/reply/nest")
    public ResponseEntity<Object> createNestReply(@RequestBody NestReplyRequest nestReplyRequest) {
        boardService.createNestReply(nestReplyRequest);

        String result = "대댓글 작성이 완료되었습니다";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @Operation(summary = "게시글 대댓글 조회", description = "특정 댓글에 대한 대댓글 조회")
    @GetMapping("/board/reply/nest/{replyId}")
    public ResponseEntity<BaseResponse<List<ReplyResponse>>> retrieveNestReply(@PathVariable Long replyId) {
        List<ReplyResponse> responses = boardService.retrieveNestReply(replyId);
        BaseResponse<List<ReplyResponse>> baseResponse = new BaseResponse<>(responses);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
