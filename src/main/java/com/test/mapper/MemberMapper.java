package com.test.mapper;

import com.test.dto.MemberDto;
import com.test.model.Member;
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
