package com.nanum.board.boardservice.board.application;

import com.nanum.board.boardservice.board.dto.BoardDto;
import com.nanum.board.boardservice.board.vo.*;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    boolean writePost(Long userId, BoardDto boardDto, List<MultipartFile> multipartFiles);

    boolean createCategories(BoardCategoryRequest boardCategoryRequest);

    List<BoardListResponse> retrievePosts(Long categoryId);

    BoardResponse retrievePost(Long postId, Long id);

    List<BoardCategoryResponse> retrieveCategories();

    boolean updateCategories(CategoryUpdateRequest categoryUpdateRequest);

    Long likePosts(Long postId);

    boolean createNestReply(NestReplyRequest nestReplyRequest);

    void deleteLike(Long recommendId);

    void updateReply(ReplyUpdateRequest replyUpdateRequest);

    void createComment(Long userId, ReplyRequest replyRequest);

    List<ReplyResponse> retrieveReply(Long boardId);

    List<ReplyResponse> retrieveNestReply(Long replyId);

    boolean updatePosts(BoardUpdateRequest boardUpdateRequest, List<MultipartFile> multipartFiles);
}
