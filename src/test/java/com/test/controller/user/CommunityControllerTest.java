package com.test.controller.user;

import com.test.database.dto.CommunityDto;
import com.test.database.dto.CommunityListDto;
import com.test.database.model.Member;
import com.test.database.model.enums.MemberRole;
import com.test.database.requests.CreateCommunityRequest;
import com.test.service.CommunityService;
import com.test.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommunityControllerTest {

    @Mock
    private CommunityService communityService;

    @Mock
    private MembershipService membershipService;

    @InjectMocks
    private CommunityController communityController;

    private CommunityListDto communityListDto;
    private CommunityDto communityDto;
    private Member testMember;

    @BeforeEach
    void setUp() {
        communityListDto = CommunityListDto.builder()
                                           .id(1L)
                                           .name("Community 1")
                                           .build();

        communityDto = CommunityDto.builder()
                                   .id(1L)
                                   .name("Community 1")
                                   .description("Description")
                                   .build();

        testMember = new Member();
        testMember.setId(1L);
    }

    @Test
    void testGetAllCommunities() {
        List<CommunityListDto> communityList = Arrays.asList(communityListDto);
        Mockito.when(communityService.getAllCommunityList()).thenReturn(communityList);

        ResponseEntity<List<CommunityListDto>> response = communityController.getAllCommunities();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Community 1", response.getBody().get(0).getName());
    }

    @Test
    void testCreateCommunity() {
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("New Community");
        request.setDescription("New Description");

        Mockito.when(membershipService.createCommunity(
                ArgumentMatchers.eq("New Community"),
                ArgumentMatchers.eq("New Description")
        )).thenReturn(communityDto);

        ResponseEntity<CommunityDto> response = communityController.createCommunity(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Community 1", response.getBody().getName());
        assertEquals("Description", response.getBody().getDescription());
    }

    @Test
    void testJoinCommunity() {
        Long communityId = 1L;

        ResponseEntity<Void> response = communityController.joinCommunity(communityId);

        Mockito.verify(membershipService).joinCommunity(communityId);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testLeaveCommunity() {
        Long communityId = 1L;

        ResponseEntity<Void> response = communityController.leaveCommunity(communityId);

        Mockito.verify(membershipService).leaveCommunity(communityId);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteCommunity() {
        Long communityId = 1L;

        ResponseEntity<String> response = communityController.deleteCommunity(communityId);

        Mockito.verify(membershipService).deleteCommunityByOwner(communityId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Сообщество успешно удалено", response.getBody());
    }

    @Test
    void testGetCommunityMembers() {
        Long communityId = 1L;
        List<Member> members = Arrays.asList(testMember);
        Mockito.when(communityService.getCommunityMembers(communityId)).thenReturn(members);

        ResponseEntity<List<Member>> response = communityController.getCommunityMembers(communityId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void testUpdateMemberRole() {
        Long communityId = 1L;
        Long memberId = 1L;
        MemberRole newRole = MemberRole.ADMIN;

        ResponseEntity<String> response = communityController.updateMemberRole(communityId, memberId, newRole);

        Mockito.verify(membershipService).updateMemberRole(communityId, memberId, newRole);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Роль участника изменена на " + newRole, response.getBody());
    }
}
