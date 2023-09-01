package ru.adel.socialmedia.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.util.exception.UserNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FriendRequestServiceImpl friendRequest;


    private User user;

    private User friend;

    @BeforeEach
    public void setup() {

        user = new User();
        user.setId(1L);
        user.setFollowers(new HashSet<>());
        user.setFollowing(new HashSet<>());
        user.setFriends(new HashSet<>());

        friend = new User();
        friend.setId(2L);
        friend.setFollowers(new HashSet<>());
        friend.setFollowing(new HashSet<>());
        friend.setFriends(new HashSet<>());


    }


    @Test
    void getFollowingUsers_ExistingUser_ReturnsListOfFollowingUsers() {
        // Arrange
        Set<User> followingUsers = new HashSet<>();
        followingUsers.add(new User());
        followingUsers.add(new User());
        user.setFollowing(followingUsers);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        // Act
        List<UserDTO> result = friendRequest.getFollowingUsers(user.getId());

        // Assert
        assertEquals(followingUsers.size(), result.size());
        verify(userRepository).findById(user.getId());
        verify(modelMapper, times(followingUsers.size())).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    void getFollowingUsers_NonExistingUser_ThrowsUserNotFoundException() {
        // Arrange
        Long userId = 4L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> friendRequest.getFollowingUsers(userId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(modelMapper);
    }


    @Test
    void getFollowersUsers_ExistingUser_ReturnsListOfFollowersUsers() {
        // Arrange
        Set<User> followers = new HashSet<>();
        followers.add(new User());
        followers.add(new User());
        user.setFollowers(followers);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());
        // Act
        List<UserDTO> followersList = friendRequest.getFollowers(user.getId());

        // Assert
        assertEquals(followers.size(), followersList.size());
        verify(userRepository).findById(user.getId());
        verify(modelMapper, times(followers.size())).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    void sendFriendRequest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());
        // Act
        UserDTO result = friendRequest.sendFriendRequest(user.getId(), friend.getId());


        // Assert
        assertNotNull(result);
        verify(userRepository).findById(user.getId());
        verify(userRepository).findById(friend.getId());
        verify(userRepository, times(2)).save(any(User.class));
        verify(modelMapper).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    void cancelFriendRequest() {
        user.getFollowing().add(friend);
        friend.getFollowers().add(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        UserDTO result = friendRequest.cancelFriendRequest(user.getId(), friend.getId());

        // Assert
        assertNotNull(result);
        assertFalse(user.getFollowing().contains(friend)); // Проверяем, что друг удалён из списка подписчиков пользователя
        assertFalse(friend.getFollowers().contains(user)); // Проверяем, что пользователь удалён из списка пользователей, на которых подписан друг
        verify(userRepository).findById(user.getId());
        verify(userRepository).findById(friend.getId());
        verify(userRepository, times(2)).save(any(User.class));
        verify(modelMapper).map(any(User.class), eq(UserDTO.class));

    }

    @Test
    void acceptFriendRequest() {
        friend.getFollowing().add(user);
        user.getFollowers().add(friend);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        UserDTO result = friendRequest.acceptFriendRequest(user.getId(), friend.getId());

        assertNotNull(result);
        assertTrue(user.getFollowing().contains(friend));
        assertTrue(friend.getFollowers().contains(user));
        assertTrue(user.getFriends().contains(friend));
        assertTrue(friend.getFriends().contains(user));

        verify(userRepository).findById(user.getId());
        verify(userRepository).findById(friend.getId());
        verify(userRepository, times(2)).save(any(User.class));
        verify(modelMapper).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    void removeFriend() {
        friend.getFriends().add(user);
        user.getFriends().add(friend);
        friend.getFollowing().add(user);
        user.getFollowing().add(friend);
        user.getFollowers().add(friend);
        friend.getFollowers().add(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        UserDTO result = friendRequest.removeFriend(user.getId(), friend.getId());


        assertNotNull(result);
        assertFalse(user.getFriends().contains(friend));
        assertFalse(friend.getFriends().contains(user));
        assertFalse(friend.getFollowers().contains(user));
        assertFalse(user.getFollowing().contains(friend));


        verify(userRepository).findById(user.getId());
        verify(userRepository).findById(friend.getId());
        verify(userRepository, times(2)).save(any(User.class));
        verify(modelMapper).map(any(User.class), eq(UserDTO.class));
    }


}




