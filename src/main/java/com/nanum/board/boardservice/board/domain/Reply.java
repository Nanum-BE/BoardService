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
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "update reply set delete_at=now() where id=?")
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Comment("댓글 작성자 식별 id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    Board board;
}
