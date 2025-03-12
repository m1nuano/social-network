package com.test.mapper;

import com.test.dto.LikeDto;
import com.test.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "user.id", target = "userId")
    LikeDto toDto(Like like);

    @Mapping(source = "userId", target = "user.id")
    Like toEntity(LikeDto likeDto);
}
