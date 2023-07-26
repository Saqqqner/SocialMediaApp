package ru.adel.socialmedia.util.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

@Schema(description = "Ответ с JWT-токеном")
public class JWTResponse {
    @Schema(description = "JWT-токен")
    private String token;
}