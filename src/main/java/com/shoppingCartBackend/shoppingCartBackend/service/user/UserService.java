package com.shoppingCartBackend.shoppingCartBackend.service.user;

import com.shoppingCartBackend.shoppingCartBackend.dto.OrderDto;
import com.shoppingCartBackend.shoppingCartBackend.dto.UserDto;
import com.shoppingCartBackend.shoppingCartBackend.exeptions.AlreadyExistsException;
import com.shoppingCartBackend.shoppingCartBackend.exeptions.ResourceNotFoundException;
import com.shoppingCartBackend.shoppingCartBackend.model.Order;
import com.shoppingCartBackend.shoppingCartBackend.model.User;
import com.shoppingCartBackend.shoppingCartBackend.repository.UserRepository;
import com.shoppingCartBackend.shoppingCartBackend.request.CreateUserRequest;
import com.shoppingCartBackend.shoppingCartBackend.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                        .map(req -> {
                            User user = new User();
                            user.setEmail(request.getEmail());
                            user.setPassword(passwordEncoder.encode(request.getPassword()));
                            user.setFirstName(request.getFirstName());
                            user.setLastName(request.getLastName());
                            return userRepository.save(user);
                        }).orElseThrow(() -> new AlreadyExistsException(request.getEmail() + " already exists"));
    }

    @Override
    public User updateUser(UpdateUserRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () ->{
                throw new ResourceNotFoundException("User not found");
        });
    }
    @Override
    public UserDto convertUserToDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
    // Make sure orders are mapped manually if needed
    if (user.getOrders() != null) {
        userDto.setOrders(
            user.getOrders().stream()
                    .map(this::convertToDto)
                    .toList()
        );
    }
    return userDto;
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    private OrderDto convertToDto(Order order) {
    return modelMapper.map(order, OrderDto.class);
}
}
