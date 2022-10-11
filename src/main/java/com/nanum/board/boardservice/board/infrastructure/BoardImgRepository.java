package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImgRepository extends JpaRepository<BoardImage, Long> {

    List<BoardImage> findAllByBoardId(Long boardId);
}
