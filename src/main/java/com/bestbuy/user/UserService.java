package com.bestbuy.user;

import com.bestbuy.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUserById(Long id) {
        User user =
                userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream().map(this::mapToResponse)
                .toList();
    }

    public UserResponse createUser(User user) {
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Transactional
    public User updateUserById(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setEmail(userDetails.getEmail());
        user.setFullname(userDetails.getFullname());

        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .role(user.getRole())
                .build();
    }
}
