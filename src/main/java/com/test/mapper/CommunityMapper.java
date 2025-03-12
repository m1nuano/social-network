package com.test.mapper;

import com.test.dto.CommunityDto;
import com.test.model.Community;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    CommunityDto toDto(Community community);

    Community toEntity(CommunityDto communityDto);
}
