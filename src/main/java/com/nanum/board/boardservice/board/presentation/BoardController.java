package com.nanum.board.boardservice.board.presentation;

import com.nanum.board.boardservice.board.application.BoardService;
import com.nanum.board.boardservice.board.dto.BoardDto;
import com.nanum.board.boardservice.board.security.jwt.JwtTokenProvider;
import com.nanum.board.boardservice.board.vo.*;
import com.nanum.board.boardservice.client.UserServiceClient;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceClient userServiceClient;

    @Operation(summary = "게시글 작성 API", description = "사용자가 게시글을 작성하기 위한 api")
    @PostMapping("/posts/{userId}")
    public ResponseEntity<Object> writePost(@Valid @PathVariable Long userId,
                                            @RequestPart BoardRequest boardRequest,
                                            @RequestPart(value = "boardImages", required = false) List<MultipartFile> boardImages) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        BoardDto boardDto = mapper.map(boardRequest, BoardDto.class);

        String result = "글 작성이 완료되었습니다";
        BaseResponse<String> baseResponse = new BaseResponse<>(result);
        if (boardImages == null) {
            boardService.writePost(userId, boardDto, null);
        } else if  (boardImages.isEmpty()){
            boardService.writePost(userId, boardDto, null);
        }
        else{
            log.info(boardImages.toString());
            log.info("******");
            boardService.writePost(userId, boardDto, boardImages);
        }
//        if (multipartFiles != null && !multipartFiles.isEmpty()) {
//            log.info(multipartFiles.toString());
//            log.info("******");
//            boardService.writePost(userId, boardDto, multipartFiles);
//        } else {
//            log.info("-------");
//            boardService.writePost(userId, boardDto, null);
//        }


        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @Operation(summary = "게시글 카테고리별 전체 목록 조회 api", description = "사용자 혹은 호스트가 게시글 목록을 조회")
    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<BaseResponse<Page<BoardListResponse>>> retrievePostsPage(@PathVariable Long categoryId,
                                                                                   @PageableDefault(sort = "createAt",
                                                                                           direction = Sort.Direction.DESC)
                                                                                   Pageable pageable) {
        Page<BoardListResponse> boardListResponses = boardService.retrievePostsV2(categoryId, pageable);
        BaseResponse<Page<BoardListResponse>> baseResponse = new BaseResponse<>(boardListResponses);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

//    @Operation(summary = "게시글 카테고리별 전체 목록 조회 api", description = "사용자 혹은 호스트가 게시글 목록을 조회")
//    @GetMapping("/posts/category/{categoryId}")
//    public ResponseEntity<BaseResponse<List<BoardListResponse>>> retrievePosts(@PathVariable Long categoryId) {
//        List<BoardListResponse> boardListRespons = boardService.retrievePosts(categoryId);
//        BaseResponse<List<BoardListResponse>> baseResponse = new BaseResponse<>(boardListRespons);
//        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
//    }

    @Operation(summary = "게시글 상세조회 api", description = "게시글 Id에 따라 상세조회하는 api")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<BaseResponse<BoardResponse>> retrievePost(@PathVariable Long postId) {

        String token = jwtTokenProvider.customResolveToken();
        BoardResponse boardResponse;
        String userPk;

        if (token == null) {
            Long userId = -1L;
            boardResponse = boardService.retrievePost(postId, userId);
        } else {
            userPk = jwtTokenProvider.getUserPk(token);
            UsersResponse users = userServiceClient.getUsersByEmail(userPk);
            Long id = users.getUserId();

            boardResponse = boardService.retrievePost(postId, id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(boardResponse));
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

        if (!board) {
            return ResponseEntity.status(HttpStatus.OK).body(failResponse);
        } else
            return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제하는 api, 삭제하면 글 목록에서도 보이지 않도록")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Object> deletePosts(@PathVariable Long postId) {
        boardService.deletePosts(postId);

        String result = "게시글 삭제 완료";
        BaseResponse<String> response = new BaseResponse<>(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
