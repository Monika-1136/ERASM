package com.erasm.core.mapper;

import com.erasm.core.dto.request.UserRequest;
import com.erasm.core.dto.response.UserResponse;
import com.erasm.core.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        if (user.getRole() != null && user.getRole().getRoleName() != null) {
            response.setRole(user.getRole().getRoleName().name());
        }
        return response;
    }

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }
}
