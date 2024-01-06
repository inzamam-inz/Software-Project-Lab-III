package com.unishare.backend.service;

import com.unishare.backend.DTO.UserResponse;
import com.unishare.backend.exceptionHandlers.ErrorMessageException;
import com.unishare.backend.model.User;
import com.unishare.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public UserResponse makeResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getFullName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("User not found with ID: " + id));
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail());
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("User not found with ID: " + id));
        userRepository.delete(user);
    }

    public UserResponse getMe(String token) {
        User user = authenticationService.getMe(token);
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail());
    }
}
