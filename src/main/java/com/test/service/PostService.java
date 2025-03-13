package com.test.service;

import com.test.database.dto.PostDto;
import com.test.database.mapper.PostMapper;
import com.test.database.model.Post;
import com.test.database.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    public PostDto toDTO(Post post) {
        return postMapper.toDto(post);
    }

    public Post toEntity(PostDto postDto) {
        return postMapper.toEntity(postDto);
    }

    @Transactional
    public Post addPost(Post post) {
        log.info("Adding new post for user ID: {}", post.getUser().getId());
        Post savedPost = postRepository.save(post);
        log.info("Post successfully added with ID: {}", savedPost.getId());
        return savedPost;
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        log.info("Fetching post with ID: {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found with ID: {}", postId);
                    return new RuntimeException("Post not found with ID: " + postId);
                });
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        log.info("Fetching all posts");
        List<Post> posts = postRepository.findAll();
        log.info("Fetched {} posts", posts.size());
        return posts;
    }

    @Transactional
    public Post updatePost(Post post) {
        log.info("Updating post with ID: {}", post.getId());
        if (!postRepository.existsById(post.getId())) {
            log.error("Post not found with ID: {}", post.getId());
            throw new RuntimeException("Post not found with ID: " + post.getId());
        }
        Post updatedPost = postRepository.save(post);
        log.info("Post with ID: {} successfully updated", post.getId());
        return updatedPost;
    }

    @Transactional
    public boolean deletePost(Long postId) {
        log.info("Deleting post with ID: {}", postId);
        if (!postRepository.existsById(postId)) {
            log.error("Post not found with ID: {}", postId);
            throw new RuntimeException("Post not found with ID: " + postId);
        }
        postRepository.deleteById(postId);
        log.info("Post with ID: {} successfully deleted", postId);
        return true;
    }
}
