package ru.adel.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель запроса регистрации")
public class RegistrationRequest {
    @Schema(description = "Имя пользователя")
    @NotNull
    @Size(min = 3, message = "Имя пользователя должно содержать не менее 3 символов")
    private String username;

    @Schema(description = "Email")
    @Email(message = "Должен быть формат почты")
    private String email;

    @Schema(description = "Пароль")
    @Size(min = 8, max = 16, message = "Размер должен находиться в диапазоне от 8 до 16 символов")
    private String password;
}
