package com.test.database.mapper;

import com.test.database.dto.PostDto;
import com.test.database.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "user.id", target = "userId")
    PostDto toDto(Post post);

    @Mapping(source = "userId", target = "user.id")
    Post toEntity(PostDto postDto);
}
