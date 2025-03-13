package com.test.service;

import com.test.database.dto.LikeDto;
import com.test.database.mapper.LikeMapper;
import com.test.database.model.Like;
import com.test.database.repository.LikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    public LikeService(LikeRepository likeRepository, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
    }

    public LikeDto toDTO(Like like) {
        return likeMapper.toDto(like);
    }

    public Like toEntity(LikeDto likeDto) {
        return likeMapper.toEntity(likeDto);
    }

    @Transactional
    public Like addLike(Like like) {
        log.info("Adding new like for object ID: {}", like.getObjectId());
        Like savedLike = likeRepository.save(like);
        log.info("Like successfully added with ID: {}", savedLike.getId());
        return savedLike;
    }

    @Transactional(readOnly = true)
    public Like getLikeById(Long likeId) {
        log.info("Fetching like with ID: {}", likeId);
        return likeRepository.findById(likeId)
                .orElseThrow(() -> {
                    log.error("Like not found with ID: {}", likeId);
                    return new RuntimeException("Like not found with ID: " + likeId);
                });
    }

    @Transactional(readOnly = true)
    public List<Like> getAllLikes() {
        log.info("Fetching all likes");
        List<Like> likes = likeRepository.findAll();
        log.info("Fetched {} likes", likes.size());
        return likes;
    }

    @Transactional
    public Like updateLike(Like like) {
        log.info("Updating like with ID: {}", like.getId());
        if (!likeRepository.existsById(like.getId())) {
            log.error("Like not found with ID: {}", like.getId());
            throw new RuntimeException("Like not found with ID: " + like.getId());
        }
        Like updatedLike = likeRepository.save(like);
        log.info("Like with ID: {} successfully updated", like.getId());
        return updatedLike;
    }

    @Transactional
    public boolean deleteLike(Long likeId) {
        log.info("Deleting like with ID: {}", likeId);
        if (!likeRepository.existsById(likeId)) {
            log.error("Like not found with ID: {}", likeId);
            throw new RuntimeException("Like not found with ID: " + likeId);
        }
        likeRepository.deleteById(likeId);
        log.info("Like with ID: {} successfully deleted", likeId);
        return true;
    }
}
