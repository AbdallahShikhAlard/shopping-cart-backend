package com.shoppingCartBackend.shoppingCartBackend.service.user;

import com.shoppingCartBackend.shoppingCartBackend.dto.UserDto;
import com.shoppingCartBackend.shoppingCartBackend.model.User;
import com.shoppingCartBackend.shoppingCartBackend.request.CreateUserRequest;
import com.shoppingCartBackend.shoppingCartBackend.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UpdateUserRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
