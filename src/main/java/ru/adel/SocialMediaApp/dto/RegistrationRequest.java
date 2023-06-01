package ru.adel.SocialMediaApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель запроса регистрации")
public class RegistrationRequest {
    @Schema(description = "Имя пользователя")
    @NotBlank
    private String username;

    @Schema(description = "Email")
    @NotBlank
    private String email;

    @Schema(description = "Пароль")
    @NotBlank
    @Size(min = 6 ,max = 12)
    private String password;
}
