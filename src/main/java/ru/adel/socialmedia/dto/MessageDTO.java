package ru.adel.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель сообщения")
public class MessageDTO {
    @Schema(description = "Идентификатор сообщения")
    private Long id;

    @Schema(description = "Текст сообщения")
    @NotNull
    private String content;

    @Schema(description = "Идентификатор получателя")
    @NotNull
    private Long recipientId;

    @Schema(description = "Идентификатор отправителя")
    private Long senderId;
}
