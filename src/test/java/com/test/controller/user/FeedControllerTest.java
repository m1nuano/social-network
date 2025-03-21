package com.test.controller.user;

import com.test.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    private Page<Map<String, Object>> testFeedpage;

    @BeforeEach
    void setUp() {
        Map<String, Object> feedItem = Map.of(
                "id", 1L,
                "title", "Test Feed Item",
                "content", "Sample content"
        );
        testFeedpage = new PageImpl<>(Collections.singletonList(feedItem), PageRequest.of(0, 10), 1);
    }

    @Test
    void testGetFeed() {
        int page = 0;
        int size = 10;

        Mockito.when(feedService.getFeed(ArgumentMatchers.eq(PageRequest.of(page, size))))
               .thenReturn(testFeedpage);

        ResponseEntity<Page<Map<String, Object>>> responseEntity = feedController.getFeed(page, size);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        Page<Map<String, Object>> resultPage = responseEntity.getBody();
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        Map<String, Object> feedItem = resultPage.getContent().get(0);
        assertEquals(1L, feedItem.get("id"));
        assertEquals("Test Feed Item", feedItem.get("title"));
        assertEquals("Sample content", feedItem.get("content"));
    }
}
