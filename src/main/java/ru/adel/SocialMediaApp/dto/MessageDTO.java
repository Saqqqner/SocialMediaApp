package ru.adel.SocialMediaApp.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class MessageDTO {
    private Long id;
    private String content;
    private Long recipientId;
    private Long senderId;

}
