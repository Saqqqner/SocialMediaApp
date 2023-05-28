package ru.adel.SocialMediaApp.services;

import ru.adel.SocialMediaApp.dto.RegistrationRequest;
import ru.adel.SocialMediaApp.dto.UserDTO;

public interface RegistrationService {
    UserDTO registerUser(RegistrationRequest registrationRequest);
    UserDTO changePassword(Long userId, String newPassword);
}
