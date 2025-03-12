package com.test.controller;

import com.test.dto.MemberDto;
import com.test.service.MemberService;
import com.test.model.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberDto>> listMembers() {
        List<MemberDto> members = memberService.getAllMembers()
                .stream()
                .map(memberService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMember(@PathVariable("id") Long id) {
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(memberService.toDTO(member));
    }

    @PostMapping
    public ResponseEntity<MemberDto> createMember(@Valid @RequestBody MemberDto memberDto) {
        Member member = memberService.toEntity(memberDto);
        Member createdMember = memberService.addMember(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.toDTO(createdMember));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable("id") Long id, @Valid @RequestBody MemberDto memberDto) {
        Member member = memberService.toEntity(memberDto);
        member.setId(id);
        Member updatedMember = memberService.updateMember(member);
        return ResponseEntity.ok(memberService.toDTO(updatedMember));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("id") Long id) {
        boolean isDeleted = memberService.deleteMember(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
