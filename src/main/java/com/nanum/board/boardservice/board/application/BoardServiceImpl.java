package com.nanum.board.boardservice.board.application;

import com.nanum.board.boardservice.board.domain.*;
import com.nanum.board.boardservice.board.dto.BoardDto;
import com.nanum.board.boardservice.board.infrastructure.*;
import com.nanum.board.boardservice.board.vo.*;
import com.nanum.board.boardservice.client.UserServiceClient;
import com.nanum.board.config.BaseResponse;
import com.nanum.board.utils.s3.S3UploaderService;
import com.nanum.board.utils.s3.dto.S3UploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardImgRepository boardImgRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final S3UploaderService s3UploaderService;
    private final LikeRepository likeRepository;
    private final ReplyRepository replyRepository;
    private final UserServiceClient userServiceClient;
    private final NestReplyRepository nestReplyRepository;

    //게시글 생성
    @Override
    public boolean writePost(Long userId, BoardDto boardDto, List<MultipartFile> multipartFiles) {

        Board board = boardRepository.save(Board.builder()
                .userId(userId)
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .boardCategory(boardCategoryRepository.findById(boardDto.getBoardCategoryId()).get())
                .viewCount(0L)
                .build());

        BoardImage boardImage;
        S3UploadDto s3UploadDto;
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty())
                try {
                    s3UploadDto = s3UploaderService.uploadBoards(multipartFile, "myspharosbucket", "boardImg");

                    boardImage = BoardImage.builder()
                            .board(board)
                            .boardImgPath(s3UploadDto.getImgUrl())
                            .originName(s3UploadDto.getOriginName())
                            .saveName(s3UploadDto.getSaveName())
                            .build();

                    boardImgRepository.save(boardImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    //카테고리 생성
    @Override
    public boolean createCategories(BoardCategoryRequest boardCategoryRequest) {
        boardCategoryRepository.save(BoardCategory.builder()
                .name(boardCategoryRequest.getName())
                .build());

        return true;
    }

    @Override
    public List<BoardListResponse> retrievePosts(Long categoryId) {
        List<Board> boards = boardRepository.findAllByBoardCategoryId(categoryId);

        List<BoardListResponse> boardListRespons = new ArrayList<>();

        boards.forEach(board -> {
            boardListRespons.add(BoardListResponse.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .categoryId(board.getBoardCategory().getId())
                    .createAt(board.getCreateAt())
                    .viewCount(board.getViewCount())
                    .build());
        });

        return boardListRespons;
    }

    //상세 게시글 조회
    @Override
    public BoardResponse retrievePost(Long postId, Long id) {
        Long likeId;

        Board board = boardRepository.findById(postId).orElseThrow();
        UserResponse users = userServiceClient.getUsers(board.getUserId());
        List<BoardImage> imageList = boardImgRepository.findAllByBoardId(postId);
        List<BoardImgResponse> boardImgResponses = new ArrayList<>();

        imageList.forEach(boardImage -> {
            boardImgResponses.add(BoardImgResponse.builder()
                    .imgId(boardImage.getId())
                    .imgUrl(boardImage.getBoardImgPath())
                    .createAt(boardImage.getCreateAt())
                    .boardId(boardImage.getBoard().getId())
                    .build());
        });

        boardRepository.save(Board.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUserId())
                .boardCategory(board.getBoardCategory())
                .viewCount(board.getViewCount() + 1)
                .build());

        log.info(String.valueOf(id));

        if (id != -1L) {
            likeId = likeRepository.findByBoardIdAndUserId(postId, id);
            if (likeId != null) {
                return BoardResponse.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .nickName(users.getResult().getNickname())
                        .content(board.getContent())
                        .recommendId(likeId)
                        .imgUrls(boardImgResponses)
                        .build();
            } else
                return BoardResponse.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .nickName(users.getResult().getNickname())
                        .content(board.getContent())
                        .recommendId(null)
                        .imgUrls(boardImgResponses)
                        .build();
        } else
            log.info("******");
            return BoardResponse.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .nickName(users.getResult().getNickname())
                    .content(board.getContent())
                    .recommendId(null)
                    .imgUrls(boardImgResponses)
                    .build();
    }

    //전체 카테고리 조회
    @Override
    public List<BoardCategoryResponse> retrieveCategories() {
        List<BoardCategory> categories = boardCategoryRepository.findAll();
        List<BoardCategoryResponse> boardCategoryResponses = new ArrayList<>();

        categories.forEach(boardCategory -> {
            boardCategoryResponses.add(BoardCategoryResponse.builder()
                    .id(boardCategory.getId())
                    .name(boardCategory.getName())
                    .build());
        });

        return boardCategoryResponses;
    }

    //카테고리 수정
    @Override
    public boolean updateCategories(CategoryUpdateRequest categoryUpdateRequest) {
        BoardCategory boardCategory = boardCategoryRepository.findById(categoryUpdateRequest.getId()).get();

        boardCategoryRepository.save(BoardCategory.builder()
                .id(boardCategory.getId())
                .name(categoryUpdateRequest.getName())
                .build());

        return true;
    }

    //게시글 좋아요 생성
    @Override
    public Long likePosts(Long postId) {
        Board board = boardRepository.findById(postId).get();

        Recommend recommend;
        Long id;

        if (likeRepository.findByBoardIdAndUserId(board.getId(), board.getUserId()) == null) {
            recommend = likeRepository.save(Recommend.builder()
                    .board(board)
                    .userId(board.getUserId())
                    .build());
            return recommend.getId();
        } else {
            id = likeRepository.findByBoardIdAndUserId(board.getId(), board.getUserId());
        }

        return id;
    }

    //특정 댓글에 대한 대댓글 생성
    @Override
    public boolean createNestReply(NestReplyRequest nestReplyRequest) {
        Reply reply = replyRepository.findById(nestReplyRequest.getReplyId()).get();

        nestReplyRepository.save(NestedReply.builder()
                .content(nestReplyRequest.getContent())
                .reply(reply)
                .userId(nestReplyRequest.getUserId())
                .build());

        return true;
    }

    //게시글 좋아요 취소
    @Override
    public void deleteLike(Long recommendId) {
        likeRepository.deleteById(recommendId);
    }

    //게시글 댓글 생성
    @Override
    public void createComment(Long userId, ReplyRequest replyRequest) {
        Board board = boardRepository.findById(replyRequest.getBoardId()).get();

        Reply reply = replyRepository.save(Reply.builder()
                .board(board)
                .userId(userId)
                .content(replyRequest.getContent())
                .build());

        replyRepository.save(Reply.builder()
                .id(reply.getId())
                .board(board)
                .content(reply.getContent())
                .userId(userId)
                .build());
    }

    //게시글 댓글들 조회
    @Override
    public List<ReplyResponse> retrieveReply(Long boardId) {
        List<ReplyResponse> replyResponses = new ArrayList<>();

        List<Reply> replies = replyRepository.findAllByBoardId(boardId);

        replies.forEach(reply -> {
            UserResponse users = userServiceClient.getUsers(reply.getUserId());

            Long countNestReply = nestReplyRepository.countAllByReplyId(reply.getId());

            replyResponses.add(ReplyResponse.builder()
                    .content(reply.getContent())
                    .nestedCount(countNestReply)
                    .replyId(reply.getId())
                    .imgUrl(users.getResult().getProfileImgUrl())
                    .createAt(reply.getCreateAt())
                    .nickName(users.getResult().getNickname())
                    .build());
        });
        return replyResponses;
    }

    //게시글 댓글 수정
    @Override
    public void updateReply(ReplyUpdateRequest replyUpdateRequest) {
        Reply reply = replyRepository.findById(replyUpdateRequest.getReplyId()).get();
        replyRepository.save(Reply.builder()
                .id(replyUpdateRequest.getReplyId())
                .userId(replyUpdateRequest.getUserId())
                .content(replyUpdateRequest.getContent())
                .board(reply.getBoard())
                .build());
    }

    //댓글에 대한 대댓글 조회
    @Override
    public List<ReplyResponse> retrieveNestReply(Long replyId) {
        List<ReplyResponse> replyResponses = new ArrayList<>();
        List<NestedReply> nestedReplies = nestReplyRepository.findAllByReplyId(replyId);

        nestedReplies.forEach(nestedReply -> {
            UserResponse users = userServiceClient.getUsers(nestedReply.getUserId());
            replyResponses.add(ReplyResponse.builder()
                    .content(nestedReply.getContent())
                    .nickName(users.getResult().getNickname())
                    .imgUrl(users.getResult().getProfileImgUrl())
                    .createAt(nestedReply.getCreateAt())
                    .replyId(nestedReply.getReply().getId())
                    .build());
        });
        return replyResponses;
    }

    //게시글 수정
    @Override
    public boolean updatePosts(BoardUpdateRequest boardUpdateRequest, List<MultipartFile> multipartFiles) {
        Board board = boardRepository.findById(boardUpdateRequest.getBoardId()).get();

        List<Long> imgId = boardUpdateRequest.getImgId();

        imgId.forEach(boardImgRepository::deleteById);

        boardRepository.save(Board.builder()
                .id(board.getId())
                .viewCount(board.getViewCount())
                .userId(board.getUserId())
                .boardCategory(boardCategoryRepository.findById(boardUpdateRequest.getCategoryId()).get())
                .content(boardUpdateRequest.getContent())
                .title(boardUpdateRequest.getTitle())
                .build());

        BoardImage boardImages;

        S3UploadDto s3UploadDto;
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
                try {
                    s3UploadDto = s3UploaderService.uploadBoards(multipartFile, "myspharosbucket", "boardImg");

                    boardImages = BoardImage.builder()
                            .board(board)
                            .boardImgPath(s3UploadDto.getImgUrl())
                            .originName(s3UploadDto.getOriginName())
                            .saveName(s3UploadDto.getSaveName())
                            .build();

                    boardImgRepository.save(boardImages);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return true;
    }
}
