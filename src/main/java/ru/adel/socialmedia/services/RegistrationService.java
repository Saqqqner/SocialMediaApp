package ru.adel.socialmedia.services;

import ru.adel.socialmedia.dto.RegistrationRequest;
import ru.adel.socialmedia.dto.UserDTO;

public interface RegistrationService {
    UserDTO registerUser(RegistrationRequest registrationRequest);

}
