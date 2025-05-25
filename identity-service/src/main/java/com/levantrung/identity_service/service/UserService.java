package com.levantrung.identity_service.service;

import com.levantrung.identity_service.dto.request.ApiResponse;
import com.levantrung.identity_service.dto.request.UserCreationRequest;
import com.levantrung.identity_service.dto.request.UserUpdationRequest;
import com.levantrung.identity_service.dto.response.UserResponse;
import com.levantrung.identity_service.entity.User;
import com.levantrung.identity_service.enums.Role;
import com.levantrung.identity_service.exception.AppException;
import com.levantrung.identity_service.exception.ErrorCode;
import com.levantrung.identity_service.mapper.UserMapper;
import com.levantrung.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    public UserResponse creatUser(UserCreationRequest request) {

        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXITSED);

        }

        User user = userMapper.toUser(request);

        //Mã hóa mật khẩu bằng thuật toán Bcrypt
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRolesFromSet(roles); // Chuyển Set<String> thành chuỗi

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponse updateUser(Long userId, UserUpdationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userMapper.updateUser(request,user);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
