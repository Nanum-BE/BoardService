package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByBoardId(Long boardId);

    Page<Reply> findAllByBoardId(Long boardId, Pageable pageable);
}
