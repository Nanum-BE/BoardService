package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAllByBoardCategoryId(Long categoryId);
}
