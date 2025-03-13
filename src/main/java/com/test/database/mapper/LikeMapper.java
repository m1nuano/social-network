package com.test.database.mapper;

import com.test.database.model.Like;
import com.test.database.dto.LikeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "user.id", target = "userId")
    LikeDto toDto(Like like);

    @Mapping(source = "userId", target = "user.id")
    Like toEntity(LikeDto likeDto);
}
