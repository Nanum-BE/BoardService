package com.nanum.board.boardservice.client;

import com.nanum.board.boardservice.board.dto.UserDto;
import com.nanum.board.boardservice.board.vo.UsersResponse;
import com.nanum.board.boardservice.client.vo.FeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping(value = "/api/v1/users/{userId}", produces = "application/json")
    FeignResponse<UserDto> getUser(@PathVariable("userId") Long userId);

    @GetMapping(value = "/api/v1/users/particular", produces = "application/json")
    FeignResponse<List<UserDto>> getUsersById(@RequestParam(value="param", required=false, defaultValue="")
                                                     List<Long> params);
    @GetMapping("/api/v1/users/email/{email}")
    UsersResponse getUsersByEmail(@PathVariable String email);

}
