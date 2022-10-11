package com.nanum.board.exception;

public class BoardImgNotFoundException extends IllegalArgumentException {
    private final static String Message = "선택한 사진이 없습니다";

    public BoardImgNotFoundException() {
        super(Message);
    }
}
