package ru.adel.SocialMediaApp.services;

import ru.adel.SocialMediaApp.dto.UserDTO;

public interface UserService {
    UserDTO getUserById(Long userId);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);

    UserDTO sendFriendRequest(Long userId, Long friendId);
    UserDTO acceptFriendRequest(Long userId, Long friendId);
    UserDTO removeFriend(Long userId, Long friendId);

}

