package com.levantrung.identity_service.exception;

import com.levantrung.identity_service.dto.request.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)   //
    ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {//truyền vào lỗi ngoại lệ cần trar

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(1001);
        apiResponse.setMessage(e.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);//Nội dung lỗi trả về
    }

    @ExceptionHandler(value = AppException.class)   //
    ResponseEntity<ApiResponse> handleAppException(AppException e) {//truyền vào lỗi ngoại lệ cần trar

        ErrorCode errorCode = e.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);//Nội dung lỗi trả về
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        // Lấy ra thông điệp lỗi mặc định (default message) từ đối tượng FieldError trong exception e
        String enumKey = e.getFieldError().getDefaultMessage();

        // Chuyển chuỗi `enumKey` thành một hằng số Enum tương ứng trong Enum `ErrorCode`
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        try {
            errorCode = ErrorCode.valueOf(enumKey);
        }
        catch (IllegalArgumentException iae) {

        }

        // Tạo một đối tượng ApiResponse mới để trả về cho client
        ApiResponse apiResponse = new ApiResponse();

        // Gán mã lỗi (code) từ ErrorCode vào đối tượng ApiResponse
        apiResponse.setCode(errorCode.getCode());
        // Gán thông điệp lỗi (message) từ ErrorCode vào đối tượng ApiResponse
        apiResponse.setMessage(errorCode.getMessage());


        return ResponseEntity.badRequest().body(apiResponse);//trả về default message
    }
}
