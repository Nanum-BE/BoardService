package com.nanum.board.boardservice.board.domain;

import com.nanum.board.config.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update board set delete_at=now() where id=?")
@Where(clause = "delete_at is null")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Comment("게시글 작성한 사용자 Id")
    private Long userId;

    @Comment("게시글 조회수")
    @Schema(defaultValue = "0")
    private Long viewCount;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Comment("속한 카테고리 Id")
    @OneToOne
    BoardCategory boardCategory;
}
