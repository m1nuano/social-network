package com.test.service;

import com.test.database.dto.CommunityPostDto;
import com.test.database.mapper.CommunityPostMapper;
import com.test.database.model.*;
import com.test.database.model.enums.MemberRole;
import com.test.database.repository.CommunityPostRepository;
import com.test.database.repository.MemberRepository;
import com.test.database.requests.CommunityPostRequest;
import com.test.database.requests.CommunityPostUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostMapper communityPostMapper;
    private final CommunityService communityService;
    private final MemberRepository memberRepository;

    public CommunityPostDto toDTO(CommunityPost communityPost) {
        return communityPostMapper.toDto(communityPost);
    }

    public CommunityPost toEntity(CommunityPostDto communityPostDto) {
        return communityPostMapper.toEntity(communityPostDto);
    }

    @Transactional
    public CommunityPost addCommunityPost(CommunityPost communityPost) {
        log.info("Adding new community post for community ID: {}", communityPost.getCommunity().getId());
        CommunityPost savedPost = communityPostRepository.save(communityPost);
        log.info("Community post successfully added with ID: {}", savedPost.getId());
        return savedPost;
    }

    @Transactional(readOnly = true)
    public CommunityPost getCommunityPostById(Long postId) {
        log.info("Fetching community post with ID: {}", postId);
        return communityPostRepository.findById(postId)
                                      .orElseThrow(() -> {
                                          log.error("Community post not found with ID: {}", postId);
                                          return new RuntimeException("Community post not found with ID: " + postId);
                                      });
    }

    @Transactional(readOnly = true)
    public List<CommunityPost> getAllCommunityPosts() {
        log.info("Fetching all community posts");
        List<CommunityPost> posts = communityPostRepository.findAll();
        log.info("Fetched {} community posts", posts.size());
        return posts;
    }

    @Transactional
    public CommunityPost updateCommunityPost(CommunityPost communityPost) {
        log.info("Updating community post with ID: {}", communityPost.getId());
        if (!communityPostRepository.existsById(communityPost.getId())) {
            log.error("Community post not found with ID: {}", communityPost.getId());
            throw new RuntimeException("Community post not found with ID: " + communityPost.getId());
        }
        CommunityPost updatedPost = communityPostRepository.save(communityPost);
        log.info("Community post with ID: {} successfully updated", updatedPost.getId());
        return updatedPost;
    }

    @Transactional
    public boolean deleteCommunityPostAsAdmin(Long postId) {
        log.info("Deleting community post with ID: {}", postId);
        if (!communityPostRepository.existsById(postId)) {
            log.error("Community post not found with ID: {}", postId);
            throw new RuntimeException("Community post not found with ID: " + postId);
        }
        communityPostRepository.deleteById(postId);
        log.info("Community post with ID: {} successfully deleted", postId);
        return true;
    }

    @Transactional(readOnly = true)
    public Page<CommunityPostDto> getCommunityPosts(Pageable pageable) {
        log.info("Fetching all posts with pagination");
        Page<CommunityPost> posts = communityPostRepository.findAll(pageable);
        return posts.map(communityPostMapper::toDto);
    }

    @Transactional
    public CommunityPostDto createCommunityPost(CommunityPostRequest request, User currentUser) {
        Community community = communityService.getCommunityById(request.getCommunityId());

        Member member = memberRepository.findByCommunityIdAndUserId(community.getId(), currentUser.getId())
                                        .orElseThrow(() -> new AccessDeniedException("You are not a member of this community"));

        if (!(member.getMemberRole() == MemberRole.ADMIN || member.getMemberRole() == MemberRole.OWNER)) {
            throw new AccessDeniedException("Only community admins and owners can post");
        }

        CommunityPost communityPost = CommunityPost.builder()
                                                   .community(community)
                                                   .user(currentUser)
                                                   .postContent(request.getPostContent())
                                                   .createdAt(LocalDateTime.now())
                                                   .build();

        CommunityPost savedPost = communityPostRepository.save(communityPost);
        log.info("Community post successfully added with ID: {}", savedPost.getId());
        return communityPostMapper.toDto(savedPost);
    }

    @Transactional
    public CommunityPostDto updateCommunityPostForUser(Long postId, CommunityPostUpdateRequest request, User currentUser) {
        CommunityPost communityPost = getCommunityPostById(postId);

        Member member = memberRepository.findByCommunityIdAndUserId(communityPost.getCommunity().getId(), currentUser.getId())
                                        .orElseThrow(() -> new AccessDeniedException("You are not a member of this community"));

        if (!(member.getMemberRole() == MemberRole.ADMIN || member.getMemberRole() == MemberRole.OWNER)) {
            throw new AccessDeniedException("Only community admins and owners can update community posts");
        }

        communityPost.setPostContent(request.getPostContent());
        CommunityPost updatedPost = communityPostRepository.save(communityPost);
        log.info("Community post with ID: {} successfully updated by user", updatedPost.getId());
        return communityPostMapper.toDto(updatedPost);
    }

    @Transactional
    public void deleteCommunityPostAsUser(Long postId, User currentUser) {
        CommunityPost communityPost = getCommunityPostById(postId);

        Member member = memberRepository.findByCommunityIdAndUserId(communityPost.getCommunity().getId(), currentUser.getId())
                                        .orElseThrow(() -> new AccessDeniedException("You are not a member of this community"));

        if (!(member.getMemberRole() == MemberRole.ADMIN || member.getMemberRole() == MemberRole.OWNER)) {
            throw new AccessDeniedException("Only community admins and owners can delete community posts");
        }

        communityPostRepository.delete(communityPost);
        log.info("Community post with ID: {} successfully deleted", postId);
    }
}
