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

@Schema(description = "Модель данных пользователя")
public class UserDTO {
    @Schema(description = "Идентификатор пользователя")
    private Long id;

    @Schema(description = "Имя пользователя")
    @NotNull
    @Size(min = 3, message = "Имя пользователя должно содержать не менее 3 символов")
    private String username;

    @Schema(description = "Email пользователя")
    @Email
    private String email;


}

