package com.example.MovieTicker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.MovieTicker.entity.User;
import com.example.MovieTicker.mapper.UserMapper;
import com.example.MovieTicker.repository.UserRepository;
import com.example.MovieTicker.request.UserCreationRequest;
import com.example.MovieTicker.response.UserResponse;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String id, UserCreationRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(existingUser, request);
        existingUser = userRepository.save(existingUser);
        return userMapper.toUserResponse(existingUser);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
}
