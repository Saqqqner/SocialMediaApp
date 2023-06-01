package ru.adel.SocialMediaApp.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.adel.SocialMediaApp.dto.MessageDTO;
import ru.adel.SocialMediaApp.security.MyUserDetails;
import ru.adel.SocialMediaApp.services.impl.MessageServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Сообщения")
public class MessageController {
    private final MessageServiceImpl messageService;

    public MessageController(MessageServiceImpl messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/{recipientId}")
    @Operation(summary = "Создание сообщения")
    @ApiResponse(responseCode = "200", description = "Сообщение успешно создано")
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO, @PathVariable Long recipientId, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        String content = messageDTO.getContent();
        MessageDTO createdMessage = messageService.createMessage(recipientId, userId, content);
        return ResponseEntity.status(HttpStatus.OK).body(createdMessage);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "Удаление сообщения")
    @ApiResponse(responseCode = "204", description = "Сообщение успешно удалено")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, Authentication authentication) {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        messageService.deleteMessage(messageId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{recipientId}")
    @Operation(summary = "Получение сообщений по отправителю и получателю")
    @ApiResponse(responseCode = "200", description = "Успешно получены сообщения")
    public ResponseEntity<List<MessageDTO>> getMessagesBySenderAndRecipient(@PathVariable Long recipientId, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        List<MessageDTO> messages = messageService.getMessagesBySenderAndRecipient(userId, recipientId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
