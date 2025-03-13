package com.test.database.mapper;

import com.test.database.model.Message;
import com.test.database.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MessageDto toDto(Message message);

    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Message toEntity(MessageDto messageDto);
}
