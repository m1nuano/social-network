package com.test.controller.user;

import com.test.database.dto.CommunityDto;
import com.test.database.dto.UserDto;
import com.test.database.dto.UserProfileDto;
import com.test.database.model.User;
import com.test.database.model.enums.FriendshipStatus;
import com.test.database.requests.EditProfileRequest;
import com.test.database.requests.UserSearchRequest;
import com.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileController profileController;

    private UserProfileDto userProfileDto;
    private UserDto userDto;
    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testUser");

        userDto = UserDto.builder()
                         .id(1L)
                         .username("testUser")
                         .email("test@example.com")
                         .firstName("John")
                         .lastName("Doe")
                         .bio("Test bio")
                         .userRole(null)    //
                         .build();

        CommunityDto community1 = CommunityDto.builder().id(1L).name("Community 1").build();
        CommunityDto community2 = CommunityDto.builder().id(2L).name("Community 2").build();

        userProfileDto = UserProfileDto.builder()
                                       .user(userDto)
                                       .friendshipStatus(FriendshipStatus.ACCEPTED)
                                       .communities(List.of(community1, community2))
                                       .build();
    }

    @Test
    void testGetProfile() {
        Mockito.when(userService.getProfile()).thenReturn(userProfileDto);

        ResponseEntity<UserProfileDto> response = profileController.getProfile();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(userDto.getUsername(), response.getBody().getUser().getUsername());
        assertEquals(FriendshipStatus.ACCEPTED, response.getBody().getFriendshipStatus());
    }

    @Test
    void testUpdateProfile() {
        EditProfileRequest request = new EditProfileRequest();
        request.setBio("Updated bio");

        Mockito.when(userService.updateProfile(any(EditProfileRequest.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = profileController.updateProfile(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(userDto.getUsername(), response.getBody().getUsername());
    }

    @Test
    void testGetAdmin() {
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        ResponseEntity<String> response = profileController.getAdmin();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("User: testUser role updated to ADMIN"));
    }

    @Test
    void testGetUserProfile() {
        Long userId = 2L;

        UserProfileDto otherUserProfile = UserProfileDto.builder()
                                                        .user(userDto)
                                                        .friendshipStatus(FriendshipStatus.PENDING)
                                                        .communities(List.of())
                                                        .build();

        Mockito.when(userService.getUserProfile(userId)).thenReturn(otherUserProfile);

        ResponseEntity<UserProfileDto> response = profileController.getUserProfile(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().getUser().getUsername());
        assertEquals(FriendshipStatus.PENDING, response.getBody().getFriendshipStatus());
        assertTrue(response.getBody().getCommunities().isEmpty());
    }

    @Test
    void testSearchUsers() {
        UserSearchRequest request = new UserSearchRequest();
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<UserProfileDto> userProfiles = new PageImpl<>(List.of(userProfileDto), pageRequest, 1);

        Mockito.when(userService.searchUserProfiles(any(UserSearchRequest.class), any(PageRequest.class)))
               .thenReturn(userProfiles);

        ResponseEntity<Page<UserProfileDto>> response = profileController.searchUsers(request, pageRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }
}
