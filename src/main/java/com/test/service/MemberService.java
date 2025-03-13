package com.test.service;

import com.test.database.dto.MemberDto;
import com.test.database.mapper.MemberMapper;
import com.test.database.model.Member;
import com.test.database.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberService(MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    public MemberDto toDTO(Member member) {
        return memberMapper.toDto(member);
    }

    public Member toEntity(MemberDto memberDto) {
        return memberMapper.toEntity(memberDto);
    }

    @Transactional
    public Member addMember(Member member) {
        log.info("Adding new member for community ID: {}", member.getCommunity().getId());
        Member savedMember = memberRepository.save(member);
        log.info("Member successfully added with ID: {}", savedMember.getId());
        return savedMember;
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long memberId) {
        log.info("Fetching member with ID: {}", memberId);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found with ID: {}", memberId);
                    return new RuntimeException("Member not found with ID: " + memberId);
                });
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        log.info("Fetching all members");
        List<Member> members = memberRepository.findAll();
        log.info("Fetched {} members", members.size());
        return members;
    }

    @Transactional
    public Member updateMember(Member member) {
        log.info("Updating member with ID: {}", member.getId());
        if (!memberRepository.existsById(member.getId())) {
            log.error("Member not found with ID: {}", member.getId());
            throw new RuntimeException("Member not found with ID: " + member.getId());
        }
        Member updatedMember = memberRepository.save(member);
        log.info("Member with ID: {} successfully updated", member.getId());
        return updatedMember;
    }

    @Transactional
    public boolean deleteMember(Long memberId) {
        log.info("Deleting member with ID: {}", memberId);
        if (!memberRepository.existsById(memberId)) {
            log.error("Member not found with ID: {}", memberId);
            throw new RuntimeException("Member not found with ID: " + memberId);
        }
        memberRepository.deleteById(memberId);
        log.info("Member with ID: {} successfully deleted", memberId);
        return true;
    }
}
