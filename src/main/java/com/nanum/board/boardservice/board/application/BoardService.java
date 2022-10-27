package com.nanum.board.boardservice.board.application;

import com.nanum.board.boardservice.board.dto.BoardDto;
import com.nanum.board.boardservice.board.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    boolean writePost(Long userId, BoardDto boardDto, List<MultipartFile> multipartFiles);

    boolean createCategories(BoardCategoryRequest boardCategoryRequest);

    List<BoardListResponse> retrievePosts(Long categoryId);

    BoardResponse retrievePost(Long postId, Long id);
     BoardResponseV2 retrievePostV2(Long postId, Long id);
    List<BoardCategoryResponse> retrieveCategories();

    boolean updateCategories(CategoryUpdateRequest categoryUpdateRequest);

    Long likePosts(Long postId);

    void deletePosts(Long postId);

    void deleteReply(Long replyId);

    ReplyResponse createNestReply(NestReplyRequest nestReplyRequest);

    void deleteLike(Long recommendId);

    void updateReply(ReplyUpdateRequest replyUpdateRequest);

    ReplyResponse createComment(Long userId, ReplyRequest replyRequest);

    List<ReplyResponse> retrieveReply(Long boardId);

    List<ReplyResponse> retrieveNestReply(Long replyId);

    void updateNestReply(NestReplyUpdateRequest nestReplyUpdateRequest);

    void deleteNestReply(Long nestId);

    boolean updatePosts(BoardUpdateRequest boardUpdateRequest, List<MultipartFile> multipartFiles);

    Page<BoardListResponse> retrievePostsV2(Long categoryId, Pageable pageable);

    Page<ReplyResponse> retrieveReplyV2(Long boardId, Pageable pageable);
}
