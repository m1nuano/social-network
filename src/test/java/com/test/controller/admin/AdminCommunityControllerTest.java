package com.test.controller.admin;

import com.test.database.dto.CommunityDto;
import com.test.database.model.Community;
import com.test.service.CommunityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCommunityControllerTest {

    @Mock
    private CommunityService communityService;

    @InjectMocks
    private AdminCommunityController adminCommunityController;

    private Community community;
    private CommunityDto communityDto;

    @BeforeEach
    void setUp() {
        community = new Community();
        community.setId(1L);
        community.setName("Test Community");

        communityDto = CommunityDto.builder()
                                   .id(1L)
                                   .name("Test Community")
                                   .description("Test Description")
                                   .build();
    }

    @Test
    void testListCommunities() {
        List<Community> communities = List.of(community);

        when(communityService.getAllCommunities()).thenReturn(communities);
        when(communityService.toDTO(community)).thenReturn(communityDto);

        ResponseEntity<List<CommunityDto>> response = adminCommunityController.listCommunities();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Community", response.getBody().get(0).getName());
    }

    @Test
    void testGetCommunity() {
        when(communityService.getCommunityById(1L)).thenReturn(community);
        when(communityService.toDTO(community)).thenReturn(communityDto);

        ResponseEntity<CommunityDto> response = adminCommunityController.getCommunity(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Community", response.getBody().getName());
    }

    @Test
    void testCreateCommunity() {
        when(communityService.toEntity(any(CommunityDto.class))).thenReturn(community);
        when(communityService.addCommunity(any(Community.class))).thenReturn(community);
        when(communityService.toDTO(any(Community.class))).thenReturn(communityDto);

        ResponseEntity<CommunityDto> response = adminCommunityController.createCommunity(communityDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Community", response.getBody().getName());
    }

    @Test
    void testUpdateCommunity() {
        when(communityService.toEntity(any(CommunityDto.class))).thenReturn(community);
        when(communityService.updateCommunity(any(Community.class))).thenReturn(community);
        when(communityService.toDTO(any(Community.class))).thenReturn(communityDto);

        ResponseEntity<CommunityDto> response = adminCommunityController.updateCommunity(1L, communityDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Community", response.getBody().getName());
    }

    @Test
    void testDeleteCommunity_Success() {
        when(communityService.deleteCommunity(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminCommunityController.deleteCommunity(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(communityService, times(1)).deleteCommunity(eq(1L));
    }

    @Test
    void testDeleteCommunity_NotFound() {
        when(communityService.deleteCommunity(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminCommunityController.deleteCommunity(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
