package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LikeRepository extends JpaRepository<Recommend, Long> {

    @Query(value = "select r.id from Recommend r where r.board.id=:boardId and r.userId=:userId")
    Long findByBoardIdAndUserId(Long boardId, Long userId);
}
