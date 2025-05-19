package com.levantrung.identity_service.mapper;

import com.levantrung.identity_service.dto.request.UserCreationRequest;
import com.levantrung.identity_service.dto.request.UserUpdationRequest;
import com.levantrung.identity_service.dto.response.UserResponse;
import com.levantrung.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    void updateUser(UserUpdationRequest request, @MappingTarget User user);

    UserResponse toUserResponse(User user);
}
