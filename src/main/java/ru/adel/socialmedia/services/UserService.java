package ru.adel.socialmedia.services;

import ru.adel.socialmedia.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserById(Long userId);

    UserDTO updateUser(Long userId, UserDTO userDTO);

    void deleteUser(Long userId);

    UserDTO changePassword(Long userId, String newPassword);

    List<UserDTO> getAllUsersExceptCurrentUser(Long currentUserId);


}

