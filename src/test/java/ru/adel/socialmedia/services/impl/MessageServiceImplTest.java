package ru.adel.socialmedia.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.adel.socialmedia.dto.MessageDTO;
import ru.adel.socialmedia.models.Message;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.MessageRepository;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.util.exception.MessageNotFoundException;
import ru.adel.socialmedia.util.exception.UnauthorizedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    @InjectMocks
    private MessageServiceImpl messageService;


    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void createMessage_ValidData_SuccessfullyCreated() {
        // Arrange
        Long recipientId = 1L;
        Long senderId = 2L;
        String content = "Hello, World!";

        User sender = new User();
        sender.setId(senderId);

        User recipient = new User();
        recipient.setId(recipientId);

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);

        Message savedMessage = new Message();
        savedMessage.setId(1L);
        savedMessage.setSender(sender);
        savedMessage.setRecipient(recipient);
        savedMessage.setContent(content);

        MessageDTO expectedMessageDTO = new MessageDTO();
        expectedMessageDTO.setId(1L);
        expectedMessageDTO.setSenderId(senderId);
        expectedMessageDTO.setRecipientId(recipientId);
        expectedMessageDTO.setContent(content);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(messageRepository.save(Mockito.any(Message.class))).thenReturn(savedMessage);
        when(modelMapper.map(savedMessage, MessageDTO.class)).thenReturn(expectedMessageDTO);

        // Act
        MessageDTO createdMessageDTO = messageService.createMessage(recipientId, senderId, content);

        // Assert
        assertEquals(expectedMessageDTO.getId(), createdMessageDTO.getId());
        assertEquals(expectedMessageDTO.getSenderId(), createdMessageDTO.getSenderId());
        assertEquals(expectedMessageDTO.getRecipientId(), createdMessageDTO.getRecipientId());
        assertEquals(expectedMessageDTO.getContent(), createdMessageDTO.getContent());

        verify(userRepository, Mockito.times(1)).findById(senderId);
        verify(userRepository, Mockito.times(1)).findById(recipientId);
        verify(messageRepository, Mockito.times(1)).save(Mockito.any(Message.class));
        verify(modelMapper, Mockito.times(1)).map(savedMessage, MessageDTO.class);
    }

    @Test
    public void deleteMessage_ValidData_MessageDeleted() {
        // Arrange
        Long messageId = 1L;
        Long userId = 2L;

        User sender = new User();
        sender.setId(userId);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        // Act
        messageService.deleteMessage(messageId, userId);

        // Assert
        verify(messageRepository, Mockito.times(1)).findById(messageId);
        verify(messageRepository, Mockito.times(1)).deleteById(messageId);
    }

    @Test
    public void deleteMessage_MessageNotFound_MessageNotFoundExceptionThrown() {
        // Arrange
        Long messageId = 1L;
        Long userId = 2L;

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(MessageNotFoundException.class, () -> messageService.deleteMessage(messageId, userId));
    }

    @Test
    public void deleteMessage_UnauthorizedUser_UnauthorizedExceptionThrown() {
        // Arrange
        Long messageId = 1L;
        Long userId = 2L;

        User sender = new User();
        sender.setId(3L);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));


        assertThrows(UnauthorizedException.class, () -> messageService.deleteMessage(messageId, userId));
    }

    @Test
    public void getMessagesBySenderAndRecipient_ValidData_ReturnsMessageDTOList() {
        // Arrange
        Long userId = 1L;
        Long recipientId = 2L;

        User sender = new User();
        sender.setId(userId);

        User recipient = new User();
        recipient.setId(recipientId);

        Message message1 = new Message();
        message1.setId(1L);
        message1.setSender(sender);
        message1.setRecipient(recipient);
        message1.setContent("Hello");

        Message message2 = new Message();
        message2.setId(2L);
        message2.setSender(recipient);
        message2.setRecipient(sender);
        message2.setContent("Hi");

        List<Message> messages = Arrays.asList(message1, message2);

        MessageDTO messageDTO1 = new MessageDTO();
        messageDTO1.setId(1L);
        messageDTO1.setSenderId(userId);
        messageDTO1.setRecipientId(recipientId);
        messageDTO1.setContent("Hello");

        MessageDTO messageDTO2 = new MessageDTO();
        messageDTO2.setId(2L);
        messageDTO2.setSenderId(recipientId);
        messageDTO2.setRecipientId(userId);
        messageDTO2.setContent("Hi");

        List<MessageDTO> expectedMessageDTOs = Arrays.asList(messageDTO1, messageDTO2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(messageRepository.findBySenderAndRecipientOrderByCreatedAtDesc(sender, recipient)).thenReturn(messages);
        when(modelMapper.map(message1, MessageDTO.class)).thenReturn(messageDTO1);
        when(modelMapper.map(message2, MessageDTO.class)).thenReturn(messageDTO2);


        List<MessageDTO> resultMessageDTOs = messageService.getMessagesBySenderAndRecipient(userId, recipientId);


        assertEquals(expectedMessageDTOs.size(), resultMessageDTOs.size());
        for (int i = 0; i < expectedMessageDTOs.size(); i++) {
            MessageDTO expectedMessageDTO = expectedMessageDTOs.get(i);
            MessageDTO resultMessageDTO = resultMessageDTOs.get(i);
            assertEquals(expectedMessageDTO.getId(), resultMessageDTO.getId());
            assertEquals(expectedMessageDTO.getSenderId(), resultMessageDTO.getSenderId());
            assertEquals(expectedMessageDTO.getRecipientId(), resultMessageDTO.getRecipientId());
            assertEquals(expectedMessageDTO.getContent(), resultMessageDTO.getContent());
        }

        verify(userRepository, Mockito.times(1)).findById(userId);
        verify(userRepository, Mockito.times(1)).findById(recipientId);
        verify(messageRepository, Mockito.times(1)).findBySenderAndRecipientOrderByCreatedAtDesc(sender, recipient);
        verify(modelMapper, Mockito.times(1)).map(message1, MessageDTO.class);
        verify(modelMapper, Mockito.times(1)).map(message2, MessageDTO.class);
    }


}
