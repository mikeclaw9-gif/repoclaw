package com.laundrypos.modules.users.service;

import com.laundrypos.modules.auth.model.User;
import com.laundrypos.modules.auth.repository.UserRepository;
import com.laundrypos.modules.users.dto.UserRequest;
import com.laundrypos.modules.users.dto.UserResponse;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        var user = User.builder()
            .username(request.username())
            .passwordHash(passwordEncoder.encode(request.password()))
            .role(User.Role.valueOf(request.role()))
            .build();

        return toResponse(userRepository.save(user));
    }

    public UserResponse update(Long id, UserRequest request) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        user.setUsername(request.username());
        user.setRole(User.Role.valueOf(request.role()));
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        userRepository.deleteById(id);
    }

    public void toggleStatus(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name(), user.isActive());
    }
}
