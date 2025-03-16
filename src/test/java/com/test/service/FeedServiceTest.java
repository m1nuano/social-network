package com.test.service;

import com.test.database.model.Community;
import com.test.database.model.CommunityPost;
import com.test.database.model.Post;
import com.test.database.model.User;
import com.test.database.repository.CommunityPostRepository;
import com.test.database.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommunityPostRepository communityPostRepository;

    @InjectMocks
    private FeedService feedService;

    @Test
    void testGetFeed_withPagination() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1L);
        when(user1.getUsername()).thenReturn("user1");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2L);
        when(user2.getUsername()).thenReturn("user2");

        Post userPost1 = mock(Post.class);
        when(userPost1.getId()).thenReturn(1L);
        when(userPost1.getPostContent()).thenReturn("User post 1");
        when(userPost1.getCreatedAt()).thenReturn(now);
        when(userPost1.getUser()).thenReturn(user1);

        Post userPost2 = mock(Post.class);
        when(userPost2.getId()).thenReturn(2L);
        when(userPost2.getPostContent()).thenReturn("User post 2");
        when(userPost2.getCreatedAt()).thenReturn(now.minusHours(2));
        when(userPost2.getUser()).thenReturn(user1);

        CommunityPost communityPost1 = mock(CommunityPost.class);
        when(communityPost1.getId()).thenReturn(3L);
        when(communityPost1.getPostContent()).thenReturn("Community post 1");
        when(communityPost1.getCreatedAt()).thenReturn(now.minusHours(1));
        when(communityPost1.getUser()).thenReturn(user2);
        Community community = mock(Community.class);
        when(community.getId()).thenReturn(101L);
        when(communityPost1.getCommunity()).thenReturn(community);

        when(postRepository.findAll()).thenReturn(Arrays.asList(userPost1, userPost2));
        when(communityPostRepository.findAll()).thenReturn(Arrays.asList(communityPost1));

        Pageable pageable = PageRequest.of(0, 2);

        Page<Map<String, Object>> feedPage = feedService.getFeed(pageable);

        assertNotNull(feedPage);
        assertEquals(3, feedPage.getTotalElements());
        assertEquals(2, feedPage.getContent().size());

        // 1) userPost1 (now)
        // 2) communityPost1 (now - 1h)
        // 3) userPost2 (now - 2h)
        Map<String, Object> firstItem = feedPage.getContent().get(0);
        Map<String, Object> secondItem = feedPage.getContent().get(1);

        // (userPost1)
        assertEquals(1L, firstItem.get("id"));
        assertEquals("User post 1", firstItem.get("content"));
        assertEquals("USER", firstItem.get("type"));
        assertEquals(1L, firstItem.get("authorId"));
        assertEquals("user1", firstItem.get("authorName"));

        // (communityPost1)
        assertEquals(3L, secondItem.get("id"));
        assertEquals("Community post 1", secondItem.get("content"));
        assertEquals("COMMUNITY", secondItem.get("type"));
        assertEquals(2L, secondItem.get("authorId"));
        assertEquals("user2", secondItem.get("authorName"));
        assertEquals(101L, secondItem.get("communityId"));

        LocalDateTime firstCreatedAt = (LocalDateTime) firstItem.get("createdAt");
        LocalDateTime secondCreatedAt = (LocalDateTime) secondItem.get("createdAt");
        assertTrue(firstCreatedAt.isAfter(secondCreatedAt) || firstCreatedAt.equals(secondCreatedAt));
    }

    @Test
    void testGetFeed_empty() {
        when(postRepository.findAll()).thenReturn(Arrays.asList());
        when(communityPostRepository.findAll()).thenReturn(Arrays.asList());

        Pageable pageable = PageRequest.of(0, 5);
        Page<Map<String, Object>> feedPage = feedService.getFeed(pageable);

        assertNotNull(feedPage);
        assertEquals(0, feedPage.getTotalElements());
        assertTrue(feedPage.getContent().isEmpty());
    }
}
