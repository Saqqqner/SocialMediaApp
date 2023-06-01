package ru.adel.SocialMediaApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    @Size(min = 6 ,max = 12)
    private String currentPassword;

    @Schema(description = "Новый пароль")
    @NotBlank
    @Size(min = 6 ,max = 12)
    private String newPassword;
}
