package com.test.service;

import com.test.database.dto.MemberDto;
import com.test.database.mapper.MemberMapper;
import com.test.database.model.Community;
import com.test.database.model.Member;
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
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberDto memberDto;

    @BeforeEach
    void setUp() {
        Community community = new Community();
        community.setId(100L);

        member = new Member();
        member.setId(1L);
        member.setCommunity(community);

        memberDto = new MemberDto();
        memberDto.setId(1L);
    }

    @Test
    void testToDTO() {
        when(memberMapper.toDto(member)).thenReturn(memberDto);
        MemberDto result = memberService.toDTO(member);
        assertNotNull(result);
        assertEquals(memberDto.getId(), result.getId());
        verify(memberMapper).toDto(member);
    }

    @Test
    void testToEntity() {
        when(memberMapper.toEntity(memberDto)).thenReturn(member);
        Member result = memberService.toEntity(memberDto);
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
        verify(memberMapper).toEntity(memberDto);
    }

    @Test
    void testAddMember() {
        when(memberRepository.save(member)).thenReturn(member);
        Member result = memberService.addMember(member);
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
        verify(memberRepository).save(member);
    }

    @Test
    void testGetMemberById_Found() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        Member result = memberService.getMemberById(member.getId());
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
        verify(memberRepository).findById(member.getId());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> memberService.getMemberById(member.getId()));
        assertTrue(exception.getMessage().contains("Member not found with ID: " + member.getId()));
        verify(memberRepository).findById(member.getId());
    }

    @Test
    void testGetAllMembers() {
        List<Member> members = Arrays.asList(member, member);
        when(memberRepository.findAll()).thenReturn(members);
        List<Member> result = memberService.getAllMembers();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(memberRepository).findAll();
    }

    @Test
    void testUpdateMember_Success() {
        when(memberRepository.existsById(member.getId())).thenReturn(true);
        when(memberRepository.save(member)).thenReturn(member);
        Member result = memberService.updateMember(member);
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
        verify(memberRepository).existsById(member.getId());
        verify(memberRepository).save(member);
    }

    @Test
    void testUpdateMember_NotFound() {
        when(memberRepository.existsById(member.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> memberService.updateMember(member));
        assertTrue(exception.getMessage().contains("Member not found with ID: " + member.getId()));
        verify(memberRepository).existsById(member.getId());
    }

    @Test
    void testDeleteMember_Success() {
        when(memberRepository.existsById(member.getId())).thenReturn(true);
        doNothing().when(memberRepository).deleteById(member.getId());
        boolean result = memberService.deleteMember(member.getId());
        assertTrue(result);
        verify(memberRepository).existsById(member.getId());
        verify(memberRepository).deleteById(member.getId());
    }

    @Test
    void testDeleteMember_NotFound() {
        when(memberRepository.existsById(member.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> memberService.deleteMember(member.getId()));
        assertTrue(exception.getMessage().contains("Member not found with ID: " + member.getId()));
        verify(memberRepository).existsById(member.getId());
        verify(memberRepository, never()).deleteById(member.getId());
    }
}
