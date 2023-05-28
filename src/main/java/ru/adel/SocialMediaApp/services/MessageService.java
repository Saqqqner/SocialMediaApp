package ru.adel.SocialMediaApp.services;

import ru.adel.SocialMediaApp.dto.MessageDTO;
import ru.adel.SocialMediaApp.dto.UserDTO;

import java.util.List;

public interface MessageService {
    MessageDTO createMessage(Long senderId,Long recipientId,String content);
    void deleteMessage(Long messageId,Long userId);
    List<MessageDTO> getMessagesBySenderAndRecipient(Long senderId, Long recipientId);
}
