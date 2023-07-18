package ru.adel.SocialMediaApp.services;

import ru.adel.SocialMediaApp.dto.UserDTO;

import java.util.List;

public interface FriendRequest {
    List<UserDTO> getFollowingUsers(Long userId);

    List<UserDTO> getFollowers(Long userId);

    UserDTO sendFriendRequest(Long userId, Long friendId);
    UserDTO acceptFriendRequest(Long userId, Long friendId);
    UserDTO removeFriend(Long userId, Long friendId);
    UserDTO cancelFriendRequest(Long userId, Long friendId);

}
