package ru.adel.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель поста")
public class PostDTO {
    @Schema(description = "Идентификатор поста")
    private Long id;

    @Schema(description = "Заголовок поста")
    @NotNull
    private String title;


    @Schema(description = "Текст поста")
    @NotNull
    private String text;

    @Schema(description = "Изображения поста")
    private Set<String> images;

    @Schema(description = "Лайки поста")
    private Long likesCount;


}
