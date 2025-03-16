package com.test.service;

import com.test.database.repository.CommunityPostRepository;
import com.test.database.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final CommunityPostRepository communityPostRepository;

    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getFeed(Pageable pageable) {
        log.info("Fetching user posts feed");
        List<Map<String, Object>> userFeedItems = postRepository.findAll().stream()
                                                                .map(post -> {
                                                                    Map<String, Object> map = new HashMap<>();
                                                                    map.put("id", post.getId());
                                                                    map.put("content", post.getPostContent());
                                                                    map.put("createdAt", post.getCreatedAt());
                                                                    map.put("authorId", post.getUser().getId());
                                                                    map.put("authorName", post.getUser().getUsername());
                                                                    map.put("type", "USER");
                                                                    return map;
                                                                })
                                                                .toList();

        log.info("Fetched {} user posts", userFeedItems.size());

        log.info("Fetching community posts feed");
        List<Map<String, Object>> communityFeedItems = communityPostRepository.findAll().stream()
                                                                              .map(post -> {
                                                                                  Map<String, Object> map = new HashMap<>();
                                                                                  map.put("id", post.getId());
                                                                                  map.put("content", post.getPostContent());
                                                                                  map.put("createdAt", post.getCreatedAt());
                                                                                  map.put("authorId", post.getUser().getId());
                                                                                  map.put("authorName", post.getUser().getUsername());
                                                                                  map.put("communityId", post.getCommunity().getId());
                                                                                  map.put("type", "COMMUNITY");
                                                                                  return map;
                                                                              })
                                                                              .toList();

        log.info("Fetched {} community posts", communityFeedItems.size());

        List<Map<String, Object>> combinedFeed = Stream.concat(userFeedItems.stream(), communityFeedItems.stream())
                                                       .sorted((m1, m2) -> ((LocalDateTime) m2.get("createdAt")).compareTo((LocalDateTime) m1.get("createdAt")))
                                                       .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combinedFeed.size());
        List<Map<String, Object>> pagedFeed = combinedFeed.subList(start, end);

        log.info("Returning page {} with {} feed items", pageable.getPageNumber(), pagedFeed.size());
        return new PageImpl<>(pagedFeed, pageable, combinedFeed.size());
    }
}
