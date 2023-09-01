package ru.adel.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель запроса аутентификации")
public class AuthenticationRequest {
    @Schema(description = "Имя пользователя")
    @Size(min = 3, message = "Имя пользователя должно содержать не менее 3 символов")
    @NotNull
    private String username;

    @Schema(description = "Пароль")
    @Size(min = 8, max = 16, message = "Размер должен находиться в диапазоне от 8 до 16 символов")
    private String password;
}