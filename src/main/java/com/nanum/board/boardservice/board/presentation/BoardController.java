package com.nanum.board.boardservice.board.presentation;

import com.nanum.board.boardservice.board.application.BoardService;
import com.nanum.board.boardservice.board.dto.BoardDto;
import com.nanum.board.boardservice.board.infrastructure.BoardRepository;
import com.nanum.board.boardservice.board.vo.BoardRequest;
import com.nanum.board.boardservice.board.vo.BoardListResponse;
import com.nanum.board.boardservice.board.vo.BoardResponse;
import com.nanum.board.boardservice.board.vo.BoardUpdateRequest;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 글 관련 api", description = "게시글 CRUD에 관한 API")
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
public class BoardController {
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @Operation(summary = "사용자 회원가입 API", description = "사용자가 회원가입을 하기 위한 요청")
    @PostMapping("/posts")
    public ResponseEntity<Object> writePost(@Valid @RequestPart BoardRequest boardRequest,
                                            @RequestPart(value = "boardImages", required = false) List<MultipartFile> multipartFiles) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        BoardDto boardDto = mapper.map(boardRequest, BoardDto.class);

        String result = "글 작성이 완료되었습니다";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            log.info(multipartFiles.toString());
            log.info("******");
            boardService.writePost(boardDto, multipartFiles);
        } else {
            log.info("-------");
            boardService.writePost(boardDto, null);
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @Operation(summary = "게시글 카테고리별 전체 목록 조회 api", description = "사용자 혹은 호스트가 게시글 목록을 조회")
    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<BaseResponse<List<BoardListResponse>>> retrievePosts(@PathVariable Long categoryId) {
        List<BoardListResponse> boardListRespons = boardService.retrievePosts(categoryId);
        BaseResponse<List<BoardListResponse>> baseResponse = new BaseResponse<>(boardListRespons);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @Operation(summary = "게시글 상세조회 api", description = "게시글 Id에 따라 상세조회하는 api")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<BaseResponse<BoardResponse>> retrievePost(@PathVariable Long postId) {
        BoardResponse boardResponse = boardService.retrievePost(postId);
        BaseResponse<BoardResponse> baseResponse = new BaseResponse<>(boardResponse);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @Operation(summary = "게시글 상세 수정 api", description = "게시글 제목, 내용, 사진 수정하는 api")
    @PutMapping("/posts")
    public ResponseEntity<Object> updatePosts(@Valid @RequestPart BoardUpdateRequest boardUpdateRequest,
                                              @RequestPart(value = "updateImg", required = false) List<MultipartFile> multipartFiles) {

        boolean board = boardService.updatePosts(boardUpdateRequest, multipartFiles);

        String result = "게시글 수정이 완료되었습니다";
        String failResult = "게시글 수정에 실패했습니다 다시 확인해주세요";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);
        BaseResponse<String> failResponse = new BaseResponse<>(failResult);

        if (!board){
            return ResponseEntity.status(HttpStatus.OK).body(failResponse);
        }else
            return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
