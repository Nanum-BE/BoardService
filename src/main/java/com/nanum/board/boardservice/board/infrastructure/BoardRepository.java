package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> ,BoardRepositoryCustom{

    List<Board> findAllByBoardCategoryId(Long categoryId);
    Page<Board> findAllByBoardCategoryId(Long categoryId,Pageable pageable);



    Page<Board> findAllByUserId(Long userId, Pageable pageable);

    Long countAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update Board b set b.viewCount = b.viewCount + 1 where b.id = :id")
    int replaceViewCount(Long id);
}
