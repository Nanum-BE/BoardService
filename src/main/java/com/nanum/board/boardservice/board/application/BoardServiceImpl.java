package com.nanum.board.boardservice.board.application;

import com.nanum.board.boardservice.board.domain.*;
import com.nanum.board.boardservice.board.dto.*;
import com.nanum.board.boardservice.board.infrastructure.*;
import com.nanum.board.boardservice.board.vo.*;
import com.nanum.board.boardservice.client.UserServiceClient;
import com.nanum.board.boardservice.client.vo.FeignResponse;
import com.nanum.board.utils.s3.S3UploaderService;
import com.nanum.board.utils.s3.dto.S3UploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if(multipartFiles==null){
            return true;
        }
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

    //상세 게시글 조회
    @Override
    public BoardResponse retrievePost(Long postId, Long id) {
        Long likeId;

        Optional<Board> byId = boardRepository.findById(postId);
        Board board = byId.get();
        log.info("sdassdad",board.getCreateAt());
        FeignResponse<UserDto> user = userServiceClient.getUser(board.getUserId());
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
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BoardResponse boardResponse = modelMapper.map(board, BoardResponse.class);

        if (id != -1L) {
            likeId = likeRepository.findByBoardIdAndUserId(postId, id);
            boardResponse.setProfileImgUrl(user.getResult().getProfileImgUrl());
                boardResponse.setNickName(user.getResult().getNickName());
                boardResponse.setImgUrls(boardImgResponses);
                boardResponse.setRecommendId(likeId);
                return boardResponse;
        }
        log.info("******");
        likeId = null;
        boardResponse.setProfileImgUrl(user.getResult().getProfileImgUrl());
        boardResponse.setNickName(user.getResult().getNickName());
        boardResponse.setImgUrls(boardImgResponses);
        boardResponse.setRecommendId(likeId);
        return boardResponse;

    }

    @Override
    public BoardResponseV2 retrievePostV2(Long postId, Long id) {
        Long likeId;

        Optional<Board> byId = boardRepository.findById(postId);
        Board board = byId.get();
        log.info("sdassdad",board.getCreateAt());
        FeignResponse<UserDto> user = userServiceClient.getUser(board.getUserId());
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


        if (id != -1L) {
            likeId = likeRepository.findByBoardIdAndUserId(postId, id);
            return BoardResponseV2.builder().board(board)
                            .recommendId(likeId)
                            .imgUrls(boardImgResponses)
                            .nickName(user.getResult().getNickName())
                    .build();

        }
        log.info("******");
        likeId = null;
        return BoardResponseV2.builder().board(board)
                .recommendId(likeId)
                .imgUrls(boardImgResponses)
                .nickName(user.getResult().getNickName())
                .build();
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



    //게시글 삭제
    @Override
    public void deletePosts(Long postId) {
        boardRepository.deleteById(postId);
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

    //게시글 좋아요 취소
    @Override
    public void deleteLike(Long recommendId) {
        likeRepository.deleteById(recommendId);
    }

    //카테고리 생성
    @Override
    public boolean createCategories(BoardCategoryRequest boardCategoryRequest) {
        boardCategoryRepository.save(BoardCategory.builder()
                .name(boardCategoryRequest.getName())
                .build());

        return true;
    }

    //카테고리별 게시글 목록 조회
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
    @Override
    public Page<BoardListResponse> retrievePostsV2(Long categoryId, Pageable pageable) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return boardRepository.findAllByBoardCategoryId(categoryId, pageable).map(board -> modelMapper.map(board, BoardListResponse.class));

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

    //게시글 댓글 생성
    @Override
    public ReplyResponse createComment(Long userId, ReplyRequest replyRequest) {
        Board board = boardRepository.findById(replyRequest.getBoardId()).get();
        FeignResponse<UserDto> users = userServiceClient.getUser(userId);
        Reply reply = replyRepository.save(Reply.builder()
                .board(board)
                .userId(userId)
                .content(replyRequest.getContent())
                .build());


       return ReplyResponse.builder()
               .content(reply.getContent())
               .nestedCount(0L)
               .replyId(reply.getId())
               .userId(reply.getUserId())
               .imgUrl(users.getResult().getProfileImgUrl())
               .createAt(reply.getCreateAt())
               .nickName(users.getResult().getNickName())
               .build();
    }
    @Override
    public Page<ReplyResponse> retrieveReplyV2(Long boardId, Pageable pageable) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Page<ReplyResponse> replyResponses = replyRepository.findAllByBoardId(boardId, pageable).map(reply -> {
            FeignResponse<UserDto> users = userServiceClient.getUser(reply.getUserId());
            Long countNestReply = nestReplyRepository.countAllByReplyId(reply.getId());

            if (reply.getDeleteAt() != null) {
                return ReplyResponse.builder()
                        .content("삭제된 댓글입니다")
                        .nestedCount(countNestReply)
                        .replyId(reply.getId())
                        .userId(reply.getUserId())
                        .imgUrl(null)
                        .createAt(reply.getCreateAt())
                        .nickName(null)
                        .build();
            } else
                return ReplyResponse.builder()
                        .content(reply.getContent())
                        .nestedCount(countNestReply)
                        .replyId(reply.getId())
                        .userId(reply.getUserId())
                        .imgUrl(users.getResult().getProfileImgUrl())
                        .createAt(reply.getCreateAt())
                        .nickName(users.getResult().getNickName())
                        .build();
        });

        return replyResponses;
    }

    @Override
    public Page<BoardCategoryDto> search(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        return boardRepository.search(boardSearchCondition,pageable);
    }

    @Override
    public BoardTotalResponse retrieveBoardTotal(Long userId) {
        Long boardCount = boardRepository.countAllByUserId(userId);
        Long replyCount = replyRepository.findReplyTotal(userId);

        return new BoardTotalResponse(boardCount, replyCount);
    }

    //게시글 댓글들 조회
    @Override
    public List<ReplyResponse> retrieveReply(Long boardId) {
        List<ReplyResponse> replyResponses = new ArrayList<>();

        List<Reply> replies = replyRepository.findAllByBoardId(boardId);

        replies.forEach(reply -> {
            FeignResponse<UserDto> users = userServiceClient.getUser(reply.getUserId());
            Long countNestReply = nestReplyRepository.countAllByReplyId(reply.getId());

            if (reply.getDeleteAt() != null) {
                replyResponses.add(ReplyResponse.builder()
                        .content("삭제된 댓글입니다")
                        .nestedCount(countNestReply)
                        .replyId(reply.getId())
                        .userId(reply.getUserId())
                        .imgUrl(null)
                        .createAt(reply.getCreateAt())
                        .nickName(null)
                        .build());
            } else
                replyResponses.add(ReplyResponse.builder()
                        .content(reply.getContent())
                        .nestedCount(countNestReply)
                        .replyId(reply.getId())
                        .userId(reply.getUserId())
                        .imgUrl(users.getResult().getProfileImgUrl())
                        .createAt(reply.getCreateAt())
                        .nickName(users.getResult().getNickName())
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

    //게시글 댓글 삭제
    @Override
    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }

    //특정 댓글에 대한 대댓글 생성
    @Override
    public ReplyResponse createNestReply(NestReplyRequest nestReplyRequest) {
        Reply reply = replyRepository.findById(nestReplyRequest.getReplyId()).get();
        FeignResponse<UserDto> users = userServiceClient.getUser(nestReplyRequest.getUserId());

        NestedReply nestedReply = nestReplyRepository.save(NestedReply.builder()
                .content(nestReplyRequest.getContent())
                .reply(reply)
                .userId(nestReplyRequest.getUserId())
                .build());

        return ReplyResponse.builder()
                .content(nestedReply.getContent())
                .nickName(users.getResult().getNickName())
                .userId(nestedReply.getUserId())
                .imgUrl(users.getResult().getProfileImgUrl())
                .createAt(nestedReply.getCreateAt())
                .replyId(nestedReply.getReply().getId())
                .build();
    }

    //댓글에 대한 대댓글 조회
    @Override
    public List<ReplyResponse> retrieveNestReply(Long replyId) {
        List<ReplyResponse> replyResponses = new ArrayList<>();
        List<NestedReply> nestedReplies = nestReplyRepository.findAllByReplyId(replyId);

        nestedReplies.forEach(nestedReply -> {
            FeignResponse<UserDto> users = userServiceClient.getUser(nestedReply.getUserId());

            if (nestedReply.getDeleteAt() != null) {
                replyResponses.add(ReplyResponse.builder()
                        .content("삭제된 댓글입니다")
                        .nickName(null)
                        .userId(nestedReply.getUserId())
                        .imgUrl(null)
                        .createAt(nestedReply.getCreateAt())
                        .replyId(nestedReply.getReply().getId())
                        .build());
            } else
                replyResponses.add(ReplyResponse.builder()
                        .content(nestedReply.getContent())
                        .nickName(users.getResult().getNickName())
                        .userId(nestedReply.getUserId())
                        .imgUrl(users.getResult().getProfileImgUrl())
                        .createAt(nestedReply.getCreateAt())
                        .replyId(nestedReply.getReply().getId())
                        .build());
        });
        return replyResponses;
    }

    //특정 댓글에 대한 대댓글 수정
    @Override
    public void updateNestReply(NestReplyUpdateRequest nestReplyUpdateRequest) {
        NestedReply nestedReply = nestReplyRepository.findById(nestReplyUpdateRequest.getNestId()).get();
        nestReplyRepository.save(NestedReply.builder()
                .reply(nestedReply.getReply())
                .content(nestReplyUpdateRequest.getContent())
                .userId(nestedReply.getUserId())
                .id(nestedReply.getId())
                .build());
    }

    //특정 댓글에 대한 대댓글 삭제
    @Override
    public void deleteNestReply(Long nestId) {
        nestReplyRepository.deleteById(nestId);
    }
}
