package com.nanum.board.boardservice.client.vo;

import lombok.Data;

@Data
public class FeignResponse<T> {
    private T result;
}