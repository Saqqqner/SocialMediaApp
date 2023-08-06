package ru.adel.socialmedia.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.adel.socialmedia.dto.MessageDTO;
import ru.adel.socialmedia.models.Message;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.MessageRepository;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.services.MessageService;
import ru.adel.socialmedia.util.exception.MessageNotFoundException;
import ru.adel.socialmedia.util.exception.UnauthorizedException;
import ru.adel.socialmedia.util.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public MessageDTO createMessage(Long recipientId, Long senderId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + senderId));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + recipientId));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);

        Message savedMessage = messageRepository.save(message);
        return modelMapper.map(savedMessage, MessageDTO.class);
    }


    @Override
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with ID: " + messageId));

        if (message.getSender().getId().equals(userId)) {
            messageRepository.deleteById(messageId);
        } else {
            throw new UnauthorizedException("User is not authorized to delete this message");
        }
    }

    @Override
    public List<MessageDTO> getMessagesBySenderAndRecipient(Long userId, Long recipientId) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + recipientId));


        List<Message> messages = messageRepository.findBySenderAndRecipientOrderByCreatedAtDesc(sender, recipient);
        return messages.stream()
                .map(message -> {
                    MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
                    messageDTO.setSenderId(message.getSender().getId());
                    messageDTO.setRecipientId(message.getRecipient().getId());
                    return messageDTO;
                })
                .collect(Collectors.toList());

    }


}
