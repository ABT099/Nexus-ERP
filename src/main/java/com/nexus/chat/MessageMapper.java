package com.nexus.chat;

import com.nexus.common.Mapper;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper implements Mapper<Message, MessageResponse> {
    @Override
    public MessageResponse map(Message source) {
        if (source == null) {
            throw new IllegalArgumentException("Source entity cannot be null");
        }

        return new MessageResponse(
                source.getId(),
                source.getSender().getId(),
                source.getChat().getId(),
                source.getText(),
                source.getCreatedAt());
    }
}
