package com.levantrung.identity_service.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_EXITSED(1001, "User is exitsed"),
    USERNAME_INVALID(1002, "Username must be at least 3 characters"),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters"),
    INVALID_KEY(1004, "Invalid key"),
    USER_NOT_EXITSED(1005, "User is not exitsed"),
    UNAUTHENTICATED(1006, "Unauthenticated"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
