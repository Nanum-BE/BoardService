package com.nanum.board.boardservice.board.application;

import com.nanum.board.boardservice.board.domain.Board;
import com.nanum.board.boardservice.board.domain.BoardCategory;
import com.nanum.board.boardservice.board.domain.BoardImage;
import com.nanum.board.boardservice.board.dto.BoardDto;
import com.nanum.board.boardservice.board.infrastructure.BoardCategoryRepository;
import com.nanum.board.boardservice.board.infrastructure.BoardImgRepository;
import com.nanum.board.boardservice.board.infrastructure.BoardRepository;
import com.nanum.board.boardservice.board.vo.*;
import com.nanum.board.utils.s3.S3UploaderService;
import com.nanum.board.utils.s3.dto.S3UploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public boolean writePost(BoardDto boardDto, List<MultipartFile> multipartFiles) {

        Board board = boardRepository.save(Board.builder()
                .userId(boardDto.getUserId())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .boardCategory(boardCategoryRepository.findById(boardDto.getBoardCategoryId()).get())
                .viewCount(0L)
                .build());

        BoardImage boardImage;
        S3UploadDto s3UploadDto;
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
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
        }


        return true;
    }

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

    @Override
    public BoardResponse retrievePost(Long postId) {
        Board board = boardRepository.findById(postId).get();

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

        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .imgUrls(boardImgResponses)
                .build();
    }

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
