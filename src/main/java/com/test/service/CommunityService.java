package com.test.service;

import com.test.database.dto.CommunityDto;
import com.test.database.mapper.CommunityMapper;
import com.test.database.model.Community;
import com.test.database.repository.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;

    public CommunityService(CommunityRepository communityRepository, CommunityMapper communityMapper) {
        this.communityRepository = communityRepository;
        this.communityMapper = communityMapper;
    }

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
}
