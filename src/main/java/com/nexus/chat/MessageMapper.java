package com.nexus.chat;

import com.nexus.utils.Mapper;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper implements Mapper<Message, MessageResponse> {
    @Override
    public MessageResponse map(Message source) {
        return new MessageResponse(
                source.getId(),
                source.getSender().getId(),
                source.getChat().getId(),
                source.getText(),
                source.getCreatedAt().toString()
        );
    }
}
