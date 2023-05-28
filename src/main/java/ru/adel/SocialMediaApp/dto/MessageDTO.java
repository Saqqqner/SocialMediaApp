package ru.adel.SocialMediaApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private String content;
    private UserDTO sender;
    private UserDTO recipient;

}
