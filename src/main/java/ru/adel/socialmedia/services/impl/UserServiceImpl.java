package ru.adel.socialmedia.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.services.UserService;
import ru.adel.socialmedia.util.exception.DuplicateUserException;
import ru.adel.socialmedia.util.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String MSG_USER = "User not found with ID: ";
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsersExceptCurrentUser(Long currentUserId) {
        List<User> allUsers = userRepository.findAll();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + currentUserId));

        return allUsers.stream()
                .filter(user -> !user.equals(currentUser))
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        // Проверка существования пользователя
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));
        checkAndUpdateEmail(existingUser, userDTO.getEmail());
        checkAndUpdateUsername(existingUser,userDTO.getUsername());
        // Обновление данных пользователя
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        // Дополнительные поля

        // Сохранение обновленных данных в базе данных
        User updatedUser = userRepository.save(existingUser);

        return modelMapper.map(updatedUser, UserDTO.class);

    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));
        // Удаление пользователя из базы данных
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserDTO changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));
        // Обновление пароля
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        // Сохранение пользователя с обновленным паролем в базе данных
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }


    private void checkAndUpdateEmail(User existingUser, String newEmail) {
        if (!existingUser.getEmail().equals(newEmail)) {
            boolean emailExists = userRepository.existsByEmail(newEmail);
            if (emailExists) {
                throw new DuplicateUserException("User with the provided email already exists");
            }
            existingUser.setEmail(newEmail);
        }
    }


    private void checkAndUpdateUsername(User existingUser, String newUsername) {
        if (!existingUser.getUsername().equals(newUsername)) {
            boolean usernameExists = userRepository.existsByUsername(newUsername);
            if (usernameExists) {
                throw new DuplicateUserException("User with the provided username already exists");
            }
            existingUser.setUsername(newUsername);
        }
    }

}

