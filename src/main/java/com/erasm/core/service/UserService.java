package com.erasm.core.service;

import com.erasm.core.dto.request.ChangePasswordRequest;
import com.erasm.core.dto.request.UserRequest;
import com.erasm.core.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    void changePassword(Long id, ChangePasswordRequest request);
}
