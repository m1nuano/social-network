package com.test.service;

import com.test.dto.UserDto;
import com.test.mapper.UserMapper;
import com.test.model.User;
import com.test.model.enums.UserRole;
import com.test.repository.UserRepository;
import com.test.security.UserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto toDTO(User user) {
        return userMapper.toDto(user);
    }

    public User toEntity(UserDto userDto) {
        return userMapper.toEntity(userDto);
    }

    @Transactional
    public User addUser(User user) {
        log.info("Adding new user: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        log.info("User successfully added with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Fetched {} users", users.size());
        return users;
    }

    @Transactional
    public User updateUser(User user) {
        log.info("Updating user with ID: {}", user.getId());
        if (!userRepository.existsById(user.getId())) {
            log.error("User not found with ID: {}", user.getId());
            throw new RuntimeException("User not found with ID: " + user.getId());
        }
        User updatedUser = userRepository.save(user);
        log.info("User with ID: {} successfully updated", user.getId());
        return updatedUser;
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User not found with ID: {}", userId);
            throw new RuntimeException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("User with ID: {} successfully deleted", userId);
        return true;
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return new UserImpl(user);
    }

    public UserDetailsService userDetailsService(){
        return this::loadUserByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void getAdmin() {
        var user = getCurrentUser();
        user.setUserRole(UserRole.ADMIN);
        userRepository.save(user);
    }
}
