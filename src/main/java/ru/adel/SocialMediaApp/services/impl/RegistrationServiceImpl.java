package ru.adel.SocialMediaApp.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.adel.SocialMediaApp.dto.RegistrationRequest;
import ru.adel.SocialMediaApp.dto.UserDTO;
import ru.adel.SocialMediaApp.models.User;
import ru.adel.SocialMediaApp.models.UserRole;
import ru.adel.SocialMediaApp.repositories.UserRepository;
import ru.adel.SocialMediaApp.services.RegistrationService;
import ru.adel.SocialMediaApp.util.exception.DuplicateUserException;
import ru.adel.SocialMediaApp.util.exception.UserNotFoundException;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO registerUser(RegistrationRequest registrationRequest) {
        User user = modelMapper.map(registrationRequest, User.class);
        boolean emailExists = userRepository.existsByEmail(user.getEmail());
        if (emailExists) {
            throw new DuplicateUserException("User with the provided email already exists");
        }

        // Проверка существования пользователя по username
        boolean usernameExists = userRepository.existsByUsername(user.getUsername());
        if (usernameExists) {
            throw new DuplicateUserException("User with the provided username already exists");
        }


        // Установка пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Установка роли пользователя
        user.setRole(UserRole.ROLE_USER);
        // Сохранение пользователя в базе данных
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }


}
