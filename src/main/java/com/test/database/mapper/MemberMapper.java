package com.test.database.mapper;

import com.test.database.dto.MemberDto;
import com.test.database.model.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "user.id", target = "userId")
    MemberDto toDto(Member member);

    @Mapping(source = "communityId", target = "community.id")
    @Mapping(source = "userId", target = "user.id")
    Member toEntity(MemberDto memberDto);
}
