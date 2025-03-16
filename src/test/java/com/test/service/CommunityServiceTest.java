package com.test.service;

import com.test.database.dto.CommunityDto;
import com.test.database.dto.CommunityListDto;
import com.test.database.mapper.CommunityMapper;
import com.test.database.model.Community;
import com.test.database.model.Member;
import com.test.database.model.User;
import com.test.database.repository.CommunityRepository;
import com.test.database.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private CommunityMapper communityMapper;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommunityService communityService;

    private Community community;
    private CommunityDto communityDto;
    private Member member;
    private User user;

    @BeforeEach
    void setUp() {
        community = new Community();
        community.setId(1L);
        community.setName("Test Community");
        community.setDescription("Test Description");

        communityDto = new CommunityDto();
        communityDto.setId(1L);
        communityDto.setName("Test Community");
        communityDto.setDescription("Test Description");

        user = new User();
        user.setId(1L);

        member = new Member();
        member.setId(1L);
        member.setCommunity(community);
    }

    @Test
    void testToDTO() {
        when(communityMapper.toDto(community)).thenReturn(communityDto);
        CommunityDto result = communityService.toDTO(community);
        assertNotNull(result);
        assertEquals(communityDto.getId(), result.getId());
        assertEquals(communityDto.getName(), result.getName());
        verify(communityMapper).toDto(community);
    }

    @Test
    void testToEntity() {
        when(communityMapper.toEntity(communityDto)).thenReturn(community);
        Community result = communityService.toEntity(communityDto);
        assertNotNull(result);
        assertEquals(community.getId(), result.getId());
        verify(communityMapper).toEntity(communityDto);
    }

    @Test
    void testAddCommunity() {
        when(communityRepository.save(community)).thenReturn(community);
        Community result = communityService.addCommunity(community);
        assertNotNull(result);
        assertEquals(community.getId(), result.getId());
        verify(communityRepository).save(community);
    }

    @Test
    void testGetCommunityById_Found() {
        when(communityRepository.findById(1L)).thenReturn(Optional.of(community));
        Community result = communityService.getCommunityById(1L);
        assertNotNull(result);
        assertEquals(community.getId(), result.getId());
        verify(communityRepository).findById(1L);
    }

    @Test
    void testGetCommunityById_NotFound() {
        when(communityRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> communityService.getCommunityById(1L));
        assertTrue(exception.getMessage().contains("Community not found with ID: 1"));
        verify(communityRepository).findById(1L);
    }

    @Test
    void testGetAllCommunities() {
        List<Community> communities = Arrays.asList(community, community);
        when(communityRepository.findAll()).thenReturn(communities);
        List<Community> result = communityService.getAllCommunities();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(communityRepository).findAll();
    }

    @Test
    void testUpdateCommunity_Success() {
        when(communityRepository.existsById(community.getId())).thenReturn(true);
        when(communityRepository.save(community)).thenReturn(community);
        Community result = communityService.updateCommunity(community);
        assertNotNull(result);
        assertEquals(community.getId(), result.getId());
        verify(communityRepository).existsById(community.getId());
        verify(communityRepository).save(community);
    }

    @Test
    void testUpdateCommunity_NotFound() {
        when(communityRepository.existsById(community.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> communityService.updateCommunity(community));
        assertTrue(exception.getMessage().contains("Community not found with ID: " + community.getId()));
        verify(communityRepository).existsById(community.getId());
    }

    @Test
    void testDeleteCommunity_Success() {
        when(communityRepository.existsById(community.getId())).thenReturn(true);
        doNothing().when(communityRepository).deleteById(community.getId());
        boolean result = communityService.deleteCommunity(community.getId());
        assertTrue(result);
        verify(communityRepository).existsById(community.getId());
        verify(communityRepository).deleteById(community.getId());
    }

    @Test
    void testDeleteCommunity_NotFound() {
        when(communityRepository.existsById(community.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> communityService.deleteCommunity(community.getId()));
        assertTrue(exception.getMessage().contains("Community not found with ID: " + community.getId()));
        verify(communityRepository).existsById(community.getId());
    }

    @Test
    void testGetAllCommunityList() {
        List<Community> communities = Arrays.asList(community);
        when(communityRepository.findAll()).thenReturn(communities);
        when(memberRepository.countByCommunityId(community.getId())).thenReturn(5L);
        List<CommunityListDto> listDtos = communityService.getAllCommunityList();
        assertNotNull(listDtos);
        assertEquals(1, listDtos.size());
        CommunityListDto dto = listDtos.get(0);
        assertEquals(community.getId(), dto.getId());
        assertEquals(community.getName(), dto.getName());
        assertEquals(community.getDescription(), dto.getDescription());
        assertEquals(5L, dto.getParticipantsCount());
        verify(communityRepository).findAll();
        verify(memberRepository).countByCommunityId(community.getId());
    }

    @Test
    void testGetCommunitiesByUser() {
        List<Community> communities = Arrays.asList(community);
        when(communityRepository.findByUserInMembers(user)).thenReturn(communities);
        when(communityMapper.toDto(community)).thenReturn(communityDto);
        List<CommunityDto> result = communityService.getCommunitiesByUser(user);
        assertNotNull(result);
        assertEquals(1, result.size());
        CommunityDto dto = result.get(0);
        assertEquals(communityDto.getId(), dto.getId());
        verify(communityRepository).findByUserInMembers(user);
        verify(communityMapper).toDto(community);
    }

    @Test
    void testGetCommunityMembers() {
        List<Member> members = Arrays.asList(member, member);
        when(memberRepository.findByCommunityId(community.getId())).thenReturn(members);
        List<Member> result = communityService.getCommunityMembers(community.getId());
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(memberRepository).findByCommunityId(community.getId());
    }
}
