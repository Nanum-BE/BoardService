package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByBoardId(Long boardId);
}
