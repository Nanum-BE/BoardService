package com.nanum.board.boardservice.board.vo;

import com.nanum.board.boardservice.board.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    UserDto result;
}
