package com.nanum.board.boardservice.board.domain;

import com.nanum.board.config.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update nested_reply set delete_at=now() where id=?")
public class NestedReply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Comment("대댓글 내용")
    private String content;

    @Column(nullable = false)
    @Comment("대댓글 작성자 id")
    private Long userId;


    @ManyToOne
    Reply reply;
}
