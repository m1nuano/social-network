package com.test.controller.admin;

import com.test.database.dto.MemberDto;
import com.test.database.model.Member;
import com.test.database.model.enums.MemberRole;
import com.test.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private AdminMemberController adminMemberController;

    private Member member;
    private MemberDto memberDto;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                       .id(1L)
                       .memberRole(MemberRole.MEMBER)
                       .joinedAt(LocalDateTime.now())
                       .build();

        memberDto = MemberDto.builder()
                             .id(1L)
                             .memberRole(MemberRole.MEMBER)
                             .joinedAt(LocalDateTime.now())
                             .build();
    }

    @Test
    void testListMembers() {
        when(memberService.getAllMembers()).thenReturn(List.of(member));
        when(memberService.toDTO(any(Member.class))).thenReturn(memberDto);

        ResponseEntity<List<MemberDto>> response = adminMemberController.listMembers();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(MemberRole.MEMBER, response.getBody().get(0).getMemberRole());
    }

    @Test
    void testGetMember() {
        when(memberService.getMemberById(1L)).thenReturn(member);
        when(memberService.toDTO(member)).thenReturn(memberDto);

        ResponseEntity<MemberDto> response = adminMemberController.getMember(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(MemberRole.MEMBER, response.getBody().getMemberRole());
    }

    @Test
    void testCreateMember() {
        when(memberService.toEntity(any(MemberDto.class))).thenReturn(member);
        when(memberService.addMember(any(Member.class))).thenReturn(member);
        when(memberService.toDTO(any(Member.class))).thenReturn(memberDto);

        ResponseEntity<MemberDto> response = adminMemberController.createMember(memberDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(MemberRole.MEMBER, response.getBody().getMemberRole());
    }

    @Test
    void testUpdateMember() {
        when(memberService.toEntity(any(MemberDto.class))).thenReturn(member);
        when(memberService.updateMember(any(Member.class))).thenReturn(member);
        when(memberService.toDTO(any(Member.class))).thenReturn(memberDto);

        ResponseEntity<MemberDto> response = adminMemberController.updateMember(1L, memberDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(MemberRole.MEMBER, response.getBody().getMemberRole());
    }

    @Test
    void testDeleteMember_Success() {
        when(memberService.deleteMember(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminMemberController.deleteMember(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(memberService, times(1)).deleteMember(eq(1L));
    }

    @Test
    void testDeleteMember_NotFound() {
        when(memberService.deleteMember(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminMemberController.deleteMember(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
