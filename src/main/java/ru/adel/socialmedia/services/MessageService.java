package ru.adel.socialmedia.services;

import ru.adel.socialmedia.dto.MessageDTO;

import java.util.List;

public interface MessageService {
    MessageDTO createMessage(Long senderId, Long recipientId, String content);

    void deleteMessage(Long messageId, Long userId);

    List<MessageDTO> getMessagesBySenderAndRecipient(Long senderId, Long recipientId);
}
