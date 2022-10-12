package com.nanum.board.boardservice.client;

import com.nanum.board.boardservice.board.vo.UserResponse;
import com.nanum.board.boardservice.board.vo.UsersResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    UserResponse getUsers(@PathVariable Long userId);

    @GetMapping("/api/v1/users/email/{email}")
    UsersResponse getUsersByEmail(@PathVariable String email);
}
