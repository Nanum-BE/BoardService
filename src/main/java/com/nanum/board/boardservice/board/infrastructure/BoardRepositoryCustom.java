package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Board;
import com.nanum.board.boardservice.board.dto.BoardCategoryDto;
import com.nanum.board.boardservice.board.dto.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom  {
    Page<BoardCategoryDto> search(BoardSearchCondition boardSearchCondition, Pageable pageable);
}
