package ru.adel.SocialMediaApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель сообщения")
public class MessageDTO {
    @Schema(description = "Идентификатор сообщения")
    @Min(1)
    private Long id;

    @Schema(description = "Текст сообщения")
    @NotBlank
    private String content;

    @Schema(description = "Идентификатор получателя")
    @Min(1)
    private Long recipientId;

    @Min(1)
    @Schema(description = "Идентификатор отправителя")
    private Long senderId;
}
