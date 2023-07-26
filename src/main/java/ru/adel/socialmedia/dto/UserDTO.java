package ru.adel.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

@Schema(description = "Модель данных пользователя")
public class UserDTO {
    @Schema(description = "Идентификатор пользователя")
    private Long id;

    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Email пользователя")
    private String email;


}

