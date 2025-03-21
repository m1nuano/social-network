package com.test.database.mapper;

import com.test.database.model.CommunityPost;
import com.test.database.dto.CommunityPostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommunityPostMapper {

    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "user.id", target = "userId")
    CommunityPostDto toDto(CommunityPost communityPost);

    @Mapping(source = "communityId", target = "community.id")
    @Mapping(source = "userId", target = "user.id")
    CommunityPost toEntity(CommunityPostDto communityPostDto);
}
