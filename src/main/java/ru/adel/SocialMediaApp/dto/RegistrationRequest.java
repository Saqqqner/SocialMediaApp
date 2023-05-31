package ru.adel.SocialMediaApp.dto;

import lombok.*;

import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class RegistrationRequest {
    private String username;
    private String email;
    private String password;

}
