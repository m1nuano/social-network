package com.test.database.mapper;

import com.test.database.model.Community;
import com.test.database.dto.CommunityDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    CommunityDto toDto(Community community);

    Community toEntity(CommunityDto communityDto);
}
