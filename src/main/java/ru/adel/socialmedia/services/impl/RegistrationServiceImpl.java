package ru.adel.socialmedia.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.adel.socialmedia.dto.RegistrationRequest;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.models.UserRole;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.services.RegistrationService;
import ru.adel.socialmedia.util.exception.DuplicateUserException;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserDTO registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new DuplicateUserException("Пользователь с указанным именем пользователя уже существует");
        }
        // Проверка существования пользователя по username
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new DuplicateUserException("Пользователь с указанным именем пользователя уже существует");
        }
        User user = modelMapper.map(registrationRequest, User.class);
        // Установка пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Установка роли пользователя
        user.setRole(UserRole.ROLE_USER);
        // Сохранение пользователя в базе данных
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }


}
