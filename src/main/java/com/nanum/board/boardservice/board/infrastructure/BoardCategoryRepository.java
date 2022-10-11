package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {
}
