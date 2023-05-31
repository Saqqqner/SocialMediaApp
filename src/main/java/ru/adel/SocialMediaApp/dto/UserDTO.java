package ru.adel.SocialMediaApp.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    // Дополнительные поля, если необходимо
    
}

