package com.nanum.board.boardservice.board.infrastructure;

import com.nanum.board.boardservice.board.domain.Board;

import com.nanum.board.boardservice.board.dto.BoardCategoryDto;
import com.nanum.board.boardservice.board.dto.BoardSearchCondition;
import com.nanum.board.boardservice.board.dto.QBoardCategoryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


import static com.nanum.board.boardservice.board.domain.QBoard.board;
import static com.nanum.board.boardservice.board.domain.QBoardCategory.boardCategory;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class BoardRepositoryImpl implements BoardRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Page<BoardCategoryDto> search(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        List<BoardCategoryDto> boardCategoryDtos = queryFactory.select(new QBoardCategoryDto(
                        board.id,
                        board.title,
                        board.content,
                        board.userId,
                        board.viewCount,
                        board.createAt,
                        boardCategory.id.as("categoryId"),
                        boardCategory.name.as("categoryName")))
                .from(board)
                .leftJoin(board.boardCategory, boardCategory)
                .where(
                        title(boardSearchCondition.getTitle()),
                        content(boardSearchCondition.getContent()),
                        categoryId(boardSearchCondition.getCategoryId()),
                        all(boardSearchCondition.getAll())
                )
                .orderBy(board.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<BoardCategoryDto> jpaQuery = queryFactory.select(new QBoardCategoryDto(
                        board.id,
                        board.title,
                        board.content,
                        board.userId,
                        board.viewCount,
                        board.createAt,
                        boardCategory.id.as("categoryId"),
                        boardCategory.name.as("categoryName")))
                .from(board)
                .leftJoin(board.boardCategory, boardCategory)
                .where(
                        title(boardSearchCondition.getTitle()),
                        content(boardSearchCondition.getContent()),
                        categoryId(boardSearchCondition.getCategoryId()),
                        all(boardSearchCondition.getAll())
                );
        return PageableExecutionUtils.getPage(boardCategoryDtos,pageable,()->jpaQuery.fetch().size());
    }

    @Override
    public Board findByBoardId(Long boardId) {
        Board board1 = queryFactory.selectFrom(board)
                .where(board.id.eq(boardId))
                .fetchFirst();

        return board1;
    }

    private BooleanExpression all(String all){
        return hasText(all) ? board.title.contains(all).or(board.content.contains(all)) : null;
    }
    private BooleanExpression title(String title){
        return hasText(title) ? board.title.contains(title) : null;
    }
    private BooleanExpression content(String content){
        return hasText(content) ? board.content.contains(content) : null;
    }
    private BooleanExpression categoryId(Long categoryId){
        return categoryId != null ? boardCategory.id.eq(categoryId) : null;
    }
}
