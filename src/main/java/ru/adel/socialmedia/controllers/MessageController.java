package ru.adel.socialmedia.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.adel.socialmedia.dto.MessageDTO;
import ru.adel.socialmedia.security.MyUserDetails;
import ru.adel.socialmedia.services.impl.MessageServiceImpl;

import javax.validation.Valid;
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
    public ResponseEntity<MessageDTO> createMessage(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestBody @Valid MessageDTO messageDTO, @PathVariable Long recipientId) {
        Long userId = myUserDetails.getUser().getId();
        String content = messageDTO.getContent();
        MessageDTO createdMessage = messageService.createMessage(recipientId, userId, content);
        return ResponseEntity.status(HttpStatus.OK).body(createdMessage);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "Удаление сообщения")
    @ApiResponse(responseCode = "204", description = "Сообщение успешно удалено")
    public ResponseEntity<Void> deleteMessage(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long messageId) {
        Long userId = myUserDetails.getUser().getId();
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{recipientId}")
    @Operation(summary = "Получение сообщений по отправителю и получателю")
    @ApiResponse(responseCode = "200", description = "Успешно получены сообщения")
    public ResponseEntity<List<MessageDTO>> getMessagesBySenderAndRecipient(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long recipientId) {
        Long userId = myUserDetails.getUser().getId();
        List<MessageDTO> messages = messageService.getMessagesBySenderAndRecipient(userId, recipientId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
