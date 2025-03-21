package com.test.database.mapper;

import com.test.database.dto.FriendshipDto;
import com.test.database.model.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId")
    FriendshipDto toDto(Friendship friendship);

    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Friendship toEntity(FriendshipDto friendshipDto);
}
