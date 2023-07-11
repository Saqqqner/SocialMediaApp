package ru.adel.SocialMediaApp.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.adel.SocialMediaApp.dto.UserDTO;
import ru.adel.SocialMediaApp.models.User;
import ru.adel.SocialMediaApp.repositories.UserRepository;
import ru.adel.SocialMediaApp.services.UserService;
import ru.adel.SocialMediaApp.util.exception.DuplicateUserException;
import ru.adel.SocialMediaApp.util.exception.IncorrectPasswordException;
import ru.adel.SocialMediaApp.util.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper ;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return modelMapper.map(user, UserDTO.class);
    }
    @Override
    public List<UserDTO> getAllUsersExceptCurrentUser(Long currentUserId) {
        List<User> allUsers = userRepository.findAll();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + currentUserId));

        return allUsers.stream()
                .filter(user -> !user.equals(currentUser))
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        // Проверка существования пользователя
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Проверка, изменились ли email и username
        if (!existingUser.getEmail().equals(userDTO.getEmail())) {
            // Проверка существования пользователя по новому email
            boolean emailExists = userRepository.existsByEmail(userDTO.getEmail());
            if (emailExists) {
                throw new DuplicateUserException("User with the provided email already exists");
            }
        }

        if (!existingUser.getUsername().equals(userDTO.getUsername())) {
            // Проверка существования пользователя по новому username
            boolean usernameExists = userRepository.existsByUsername(userDTO.getUsername());
            if (usernameExists) {
                throw new DuplicateUserException("User with the provided username already exists");
            }
        }


        // Обновление данных пользователя
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        // Дополнительные поля

        // Сохранение обновленных данных в базе данных
        User updatedUser = userRepository.save(existingUser);

        return modelMapper.map(updatedUser, UserDTO.class);

    }
        @Override
        public void deleteUser (Long userId){
            // Проверка существования пользователя
            userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            // Удаление пользователя из базы данных
            userRepository.deleteById(userId);
        }
    @Override
    public List<UserDTO> getFollowingUsers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return user.getFollowing().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return user.getFollowers().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO sendFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));

        if (user.getFollowers().contains(friend)) {
            // Если пользователь уже является подписчиком, вызываем метод acceptFriendRequest
            return acceptFriendRequest(userId, friendId);
        }

        user.getFollowing().add(friend);//Добавляем друга в список пользователей на которых подписан данный пользователь
        friend.getFollowers().add(user); // Добавляем пользователя в список подписчиков друга

        userRepository.save(user);
        userRepository.save(friend);

        return modelMapper.map(user, UserDTO.class);
    }
    @Override
    public UserDTO cancelFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));

        if (user.getFollowing().contains(friend)) {
            user.getFollowing().remove(friend); // Удаляем пользователя из списка подписчиков друга
            friend.getFollowers().remove(user); // Удаляем друга из списка пользователей, на которых подписан текущий пользователь


            userRepository.save(user);
            userRepository.save(friend);
        }

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO acceptFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));

        if (!user.getFriends().contains(friend)) {
            user.getFriends().add(friend);//Добавляем друга в список друзей пользователя
            friend.getFriends().add(user); //Добавляем пользователя в список друзей данного друга
            friend.getFollowers().add(user);//Добавляем пользователя в список пользователей на которых подписан друг
            user.getFollowing().add(friend); // Добавляем друга в список подписчиков пользователя
            userRepository.save(user);
            userRepository.save(friend);
        }

        return modelMapper.map(user, UserDTO.class);
    }
    @Override
    public UserDTO removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));

        if (user.getFriends().contains(friend)) {
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);

            // Удаляем пользователя из списка подписчиков друга
            friend.getFollowers().remove(user);

            // Удаляем друга из списка пользователей, на которых подписан текущий пользователь
            user.getFollowing().remove(friend);

            userRepository.save(user);
            userRepository.save(friend);
        }

        return modelMapper.map(user, UserDTO.class);
    }
    @Override
    public UserDTO changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));



        // Обновление пароля
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        // Сохранение пользователя с обновленным паролем в базе данных
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }
}
