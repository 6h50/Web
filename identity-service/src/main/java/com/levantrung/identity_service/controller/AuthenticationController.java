package com.levantrung.identity_service.controller;


import com.levantrung.identity_service.dto.request.ApiResponse;
import com.levantrung.identity_service.dto.request.AuthenticationRequest;
import com.levantrung.identity_service.dto.request.IntrospectRequest;
import com.levantrung.identity_service.dto.response.AuthenticationResponse;
import com.levantrung.identity_service.dto.response.IntrospectResponse;
import com.levantrung.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
        // Phương thức login nhận một yêu cầu đăng nhập (AuthenticationRequest) từ phía client
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {

        // Gọi phương thức authenticate trong authenticationService để kiểm tra thông tin đăng nhập
        // Kết quả trả về là true nếu xác thực thành công, false nếu thất bại
        var result = authenticationService.authenticate(authenticationRequest);

        // Tạo đối tượng ApiResponse<AuthenticationResponse> để trả về client
        return ApiResponse.<AuthenticationResponse>builder()

                // Đặt phần kết quả (result) là một đối tượng AuthenticationResponse với thuộc tính authenticated
                .result(result)
                .code(1000)
                .build(); // Xây dựng đối tượng ApiResponse hoàn chỉnh
    }

    @PostMapping("/introspect")
        // Phương thức login nhận một yêu cầu đăng nhập (AuthenticationRequest) từ phía client
    ApiResponse<IntrospectResponse> login(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspectRequest);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .code(1000)
                .build();
    }
}
