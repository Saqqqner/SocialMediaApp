package ru.adel.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель запроса смены пароля")
public class ChangePasswordDTO {
    @Schema(description = "Идентификатор пользователя")
    private Long userId;

    @Schema(description = "Текущий пароль")
    @Size(min = 8, max = 16, message = "Размер должен находиться в диапазоне от 8 до 16 символов")
    private String currentPassword;

    @Schema(description = "Новый пароль")
    @Size(min = 8, max = 16, message = "Размер должен находиться в диапазоне от 8 до 16 символов")
    private String newPassword;
}
