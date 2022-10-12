package com.nanum.board.boardservice.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nanum.board.config.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long userId;
    private String email;
    private String pwd;
    private String nickname;
    private Role role;
    private String phone;
    private String profileImgUrl;
    private LocalDateTime createAt;
    private String gender;
    private boolean isNoteReject;

}