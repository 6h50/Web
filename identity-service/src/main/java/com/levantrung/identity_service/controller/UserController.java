package com.levantrung.identity_service.controller;

import com.levantrung.identity_service.dto.request.ApiResponse;
import com.levantrung.identity_service.dto.request.UserCreationRequest;
import com.levantrung.identity_service.dto.request.UserUpdationRequest;
import com.levantrung.identity_service.dto.response.UserResponse;
import com.levantrung.identity_service.entity.User;
import com.levantrung.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.creatUser(request));

        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable(name = "userId") Long userId) {
        return  userService.getUser(userId);
    }

    @PutMapping("/{userId}")   //UPDATE
    UserResponse updateUser(@PathVariable(name = "userId") Long userId,@RequestBody UserUpdationRequest request) {
        return  userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);
        return "User deleted has id: " + userId;
    }
}
