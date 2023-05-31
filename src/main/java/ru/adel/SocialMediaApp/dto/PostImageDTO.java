package ru.adel.SocialMediaApp.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class PostImageDTO {
    private Long id;
    private String imageUrl;
    private PostDTO post;
}
