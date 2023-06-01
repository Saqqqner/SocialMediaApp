package ru.adel.SocialMediaApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель поста")
public class PostDTO {
    @Schema(description = "Идентификатор поста")
    @Min(1)
    private Long id;


    @Schema(description = "Заголовок поста")
    @NotNull
    private String title;


    @Schema(description = "Текст поста")
    @NotNull
    private String text;

    @Schema(description = "Изображения поста")
    @NotNull
    private Set<String> images;
}
