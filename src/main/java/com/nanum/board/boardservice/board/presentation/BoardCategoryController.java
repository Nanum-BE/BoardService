package com.nanum.board.boardservice.board.presentation;

import com.nanum.board.boardservice.board.application.BoardService;
import com.nanum.board.boardservice.board.vo.BoardCategoryRequest;
import com.nanum.board.boardservice.board.vo.BoardCategoryResponse;
import com.nanum.board.boardservice.board.vo.CategoryUpdateRequest;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 글 카테고리 관련 api", description = "게시글 카테고리 API")
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
public class BoardCategoryController {
    private final BoardService boardService;

    @Operation(summary = "게시판 카테고리 생성 api", description = "카테고리 생성 api")
    @PostMapping("/categories")
    public ResponseEntity<Object> createCategories(@Valid @RequestBody BoardCategoryRequest boardCategoryRequest) {

        boolean categories = boardService.createCategories(boardCategoryRequest);

        String result = "카테고리 생성이 완료되었습니다";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);
        String failResult = "카테고리 생성에 실패했습니다. 다시 확인해주세요";
        BaseResponse<String> failResponse = new BaseResponse<>(failResult);

        if (categories) {
            return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(failResponse);
    }

    @Operation(summary = "게시판 카테고리 목록 조회 api", description = "게시판의 전체 카테고리 조회")
    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<List<BoardCategoryResponse>>> retrieveCategories() {
        List<BoardCategoryResponse> categories = boardService.retrieveCategories();

        BaseResponse<List<BoardCategoryResponse>> baseResponse = new BaseResponse<>(categories);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @Operation(summary = "게시판 카테고리 수정 api", description = "게시판의 특정 카테고리 수정")
    @PutMapping("/categories")
    public ResponseEntity<Object> updateCategories(@RequestBody CategoryUpdateRequest categoryUpdateRequest){
        boardService.updateCategories(categoryUpdateRequest);

        String result = "카테고리 수정이 완료되었습니다";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);

        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
