package com.nanum.board.boardservice.board.application;

import com.nanum.board.boardservice.board.dto.BoardDto;
import com.nanum.board.boardservice.board.vo.BoardCategoryRequest;
import com.nanum.board.boardservice.board.vo.BoardListResponse;
import com.nanum.board.boardservice.board.vo.BoardResponse;
import com.nanum.board.boardservice.board.vo.BoardUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    boolean writePost(BoardDto boardDto, List<MultipartFile> multipartFiles);

    boolean createCategories(BoardCategoryRequest boardCategoryRequest);

    List<BoardListResponse> retrievePosts(Long categoryId);

    BoardResponse retrievePost(Long postId);

    boolean updatePosts(BoardUpdateRequest boardUpdateRequest, List<MultipartFile> multipartFiles);
}
