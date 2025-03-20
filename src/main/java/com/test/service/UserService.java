package com.test.service;

import com.test.database.dto.CommunityDto;
import com.test.database.dto.UserDto;
import com.test.database.dto.UserProfileDto;
import com.test.database.mapper.UserMapper;
import com.test.database.model.Friendship;
import com.test.database.model.User;
import com.test.database.model.enums.UserRole;
import com.test.database.repository.UserRepository;
import com.test.database.requests.EditProfileRequest;
import com.test.database.requests.UserSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RelationshipService relationshipService;
    private final CommunityService communityService;

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

    @Transactional
    public UserDto updateProfile(EditProfileRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Updating profile for user: {}", currentUsername);
        User user = userRepository.findByUsername(currentUsername)
                                  .orElseThrow(() -> new UsernameNotFoundException("The user was not found"));

        boolean isUpdated = false;

        if (!request.getEmail().equals(user.getEmail())) {
            user.setEmail(request.getEmail());
            isUpdated = true;
        }
        if (!request.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(request.getFirstName());
            isUpdated = true;
        }
        if (!request.getLastName().equals(user.getLastName())) {
            user.setLastName(request.getLastName());
            isUpdated = true;
        }
        if (!request.getBio().equals(user.getBio())) {
            user.setBio(request.getBio());
            isUpdated = true;
        }

        if (isUpdated) {
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("User {} successfully updated profile", currentUsername);
        } else {
            log.info("User {} tried to update profile, but no changes were made", currentUsername);
        }

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile() {
        User user = getCurrentUser();
        log.info("Fetching profile for user ID: {}", user.getId());
        return createUserProfile(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        log.info("Fetching profile for user ID: {}", userId);
        User user = getUserById(userId);
        return createUserProfile(user);
    }

    @Transactional(readOnly = true)
    public Page<UserProfileDto> searchUserProfiles(UserSearchRequest request, Pageable pageable) {
        log.info("Searching user profiles with criteria: username={}, email={}, firstName={}, lastName={}",
                request.getUsername(), request.getEmail(), request.getFirstName(), request.getLastName());
        Page<User> page = userRepository.searchUsers(
                isNotEmpty(request.getUsername()) ? request.getUsername() : "",
                isNotEmpty(request.getEmail()) ? request.getEmail() : "",
                isNotEmpty(request.getFirstName()) ? request.getFirstName() : "",
                isNotEmpty(request.getLastName()) ? request.getLastName() : "",
                pageable
        );
        return page.map(this::createUserProfile);
    }

    private UserProfileDto createUserProfile(User user) {
        UserDto userDto = toDTO(user);
        User currentUser = getCurrentUser();

        Friendship friendship = relationshipService.getFriendshipBetween(currentUser.getId(), user.getId());
        var friendshipStatus = (friendship != null) ? friendship.getStatus() : null;

        List<CommunityDto> communities = communityService.getCommunitiesByUser(user);

        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setUser(userDto);
        profileDto.setFriendshipStatus(friendshipStatus);
        profileDto.setCommunities(communities);
        return profileDto;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public User getByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                             .orElseThrow(() -> new UsernameNotFoundException("The user was not found"));
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching current user: {}", username);
        return getByUsername(username);
    }

    public boolean existsByUsername(String username) {
        log.info("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }

    public void getAdmin() {
        User user = getCurrentUser();
        user.setUserRole(UserRole.ROLE_ADMIN);
        userRepository.save(user);
        log.info("User {} promoted to admin", user.getUsername());
    }
}
