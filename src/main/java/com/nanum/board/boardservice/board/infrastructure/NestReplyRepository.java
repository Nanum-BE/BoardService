package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.NestedReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NestReplyRepository extends JpaRepository<NestedReply, Long> {

    List<NestedReply> findAllByReplyId(Long replyId);

    @Query(value = "select count(n.id) from NestedReply n where n.reply.id=:replyId")
    Long countAllByReplyId(Long replyId);
}
