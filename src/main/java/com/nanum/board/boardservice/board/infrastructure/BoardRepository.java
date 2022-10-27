package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> ,BoardRepositoryCustom{

    List<Board> findAllByBoardCategoryId(Long categoryId);
    Page<Board> findAllByBoardCategoryId(Long categoryId,Pageable pageable);
}
