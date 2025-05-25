package com.levantrung.identity_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) //những thuộc tính là NULL sẽ không hiển thị
public class ApiResponse<T> {
    int code = 1000;
    String message;
    T result;
}
