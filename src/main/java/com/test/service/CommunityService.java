package com.test.service;

import com.test.database.dto.CommunityDto;
import com.test.database.dto.CommunityListDto;
import com.test.database.mapper.CommunityMapper;
import com.test.database.model.Community;
import com.test.database.model.Member;
import com.test.database.model.User;
import com.test.database.repository.CommunityRepository;
import com.test.database.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;
    private final MemberRepository memberRepository;

    public CommunityDto toDTO(Community community) {
        return communityMapper.toDto(community);
    }

    public Community toEntity(CommunityDto communityDto) {
        return communityMapper.toEntity(communityDto);
    }

    @Transactional
    public Community addCommunity(Community community) {
        log.info("Adding new community: {}", community.getName());
        Community savedCommunity = communityRepository.save(community);
        log.info("Community '{}' successfully added with ID: {}", community.getName(), savedCommunity.getId());
        return savedCommunity;
    }

    @Transactional(readOnly = true)
    public Community getCommunityById(Long communityId) {
        log.info("Fetching community with ID: {}", communityId);
        return communityRepository.findById(communityId)
                                  .orElseThrow(() -> {
                                      log.error("Community not found with ID: {}", communityId);
                                      return new RuntimeException("Community not found with ID: " + communityId);
                                  });
    }

    @Transactional(readOnly = true)
    public List<Community> getAllCommunities() {
        log.info("Fetching all communities");
        List<Community> communities = communityRepository.findAll();
        log.info("Fetched {} communities", communities.size());
        return communities;
    }

    @Transactional
    public Community updateCommunity(Community community) {
        log.info("Updating community with ID: {}", community.getId());
        if (!communityRepository.existsById(community.getId())) {
            log.error("Community not found with ID: {}", community.getId());
            throw new RuntimeException("Community not found with ID: " + community.getId());
        }
        Community updatedCommunity = communityRepository.save(community);
        log.info("Community with ID: {} successfully updated", community.getId());
        return updatedCommunity;
    }

    @Transactional
    public boolean deleteCommunity(Long communityId) {
        log.info("Deleting community with ID: {}", communityId);
        if (!communityRepository.existsById(communityId)) {
            log.error("Community not found with ID: {}", communityId);
            throw new RuntimeException("Community not found with ID: " + communityId);
        }
        communityRepository.deleteById(communityId);
        log.info("Community with ID: {} successfully deleted", communityId);
        return true;
    }

    @Transactional(readOnly = true)
    public List<CommunityListDto> getAllCommunityList() {
        log.info("Fetching all community lists");
        List<Community> communities = communityRepository.findAll();
        return communities.stream().map(community -> {
            Long count = memberRepository.countByCommunityId(community.getId());
            return CommunityListDto.builder()
                                   .id(community.getId())
                                   .name(community.getName())
                                   .description(community.getDescription())
                                   .participantsCount(count)
                                   .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityDto> getCommunitiesByUser(User user) {
        log.info("Fetching communities for user with ID: {}", user.getId());
        List<Community> communities = communityRepository.findByUserInMembers(user);
        return communities.stream()
                          .map(communityMapper::toDto)
                          .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Member> getCommunityMembers(Long communityId) {
        log.info("Fetching members for community with ID: {}", communityId);
        return memberRepository.findByCommunityId(communityId);
    }
}
