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
@Schema(description = "Модель запроса аутентификации")
public class AuthenticationRequest {
    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Пароль")
    @Size(min = 6 ,max = 12)
    private String password;
}