package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByBoardId(Long boardId);

    Page<Reply> findAllByBoardId(Long boardId, Pageable pageable);

    @Query(value = "select count(r) " +
            "from Reply r left outer join NestedReply nr on r.id = nr.reply.id " +
            "where r.userId =:userId or nr.userId =:userId")
    Long findReplyTotal(Long userId);
}
