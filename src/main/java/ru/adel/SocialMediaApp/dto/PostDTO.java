package ru.adel.SocialMediaApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String text;
    private UserDTO user;
    private Set<String> imageUrls;

}
