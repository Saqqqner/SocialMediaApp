package ru.adel.SocialMediaApp.services;

import ru.adel.SocialMediaApp.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserById(Long userId);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);
    UserDTO changePassword(Long userId,String newPassword);
    List<UserDTO> getAllUsersExceptCurrentUser(Long currentUserId);
    List<UserDTO> getFollowingUsers(Long userId);

    List<UserDTO> getFollowers(Long userId);

    UserDTO sendFriendRequest(Long userId, Long friendId);
    UserDTO acceptFriendRequest(Long userId, Long friendId);
    UserDTO removeFriend(Long userId, Long friendId);
    UserDTO cancelFriendRequest(Long userId, Long friendId);

}

