package ru.adel.SocialMediaApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель изображения поста")
public class PostImageDTO {
    @Schema(description = "Идентификатор изображения")
    private Long id;

    @Schema(description = "URL изображения")
    @NotNull
    private String imageUrl;

    @Schema(description = "Пост")
    @NotNull
    private PostDTO post;
}
