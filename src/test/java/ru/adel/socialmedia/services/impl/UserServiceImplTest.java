package ru.adel.socialmedia.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.util.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void getUserById_ExistingUser_Success() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("Adel");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("Adel");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(userId);

        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getUsername(), result.getUsername());

        verify(userRepository).findById(userId);
        verify(modelMapper).map(user, UserDTO.class);

    }

    @Test
    void getUserById_NonExistingUser_ThrowsUserNotFoundException() {
        Long fakeUserId = 1L;
        Long userId = 3L;

        User user = new User();
        user.setId(userId);

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(fakeUserId));

        verify(userRepository).findById(fakeUserId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsersExceptCurrentUser_NotEmptyList_Success() {
        // Arrange
        Long currentUserId = 1L;

        List<User> allUsers = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        user1.setId(2L);
        user2.setId(3L);
        user3.setId(4L);

        allUsers.add(user1);
        allUsers.add(user2);
        allUsers.add(user3);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(userRepository.findAll()).thenReturn(allUsers);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            // Здесь вы можете заполнить другие поля userDTO
            return userDTO;
        });


        // Act
        List<UserDTO> result = userService.getAllUsersExceptCurrentUser(currentUserId);

        // Assert
        assertEquals(3, result.size());
        assertFalse(result.stream().anyMatch(userDTO -> userDTO.getId().equals(currentUserId)));

        verify(userRepository).findAll();
        verify(userRepository).findById(currentUserId);
        verify(modelMapper, times(3)).map(any(User.class), eq(UserDTO.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsersExceptCurrentUser_EmptyList_Success() {
        // Arrange
        Long currentUserId = 1L;

        List<User> allUsers = new ArrayList<>();
        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(userRepository.findAll()).thenReturn(allUsers);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));


        // Act
        List<UserDTO> result = userService.getAllUsersExceptCurrentUser(currentUserId);

        // Assert
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verify(userRepository).findById(currentUserId);
        verifyNoMoreInteractions(userRepository);

    }

    @Test
    void updateUser_ExistingUser_Success() {
        // Arrange
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newemail@example.com");
        userDTO.setUsername("newusername");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("oldemail@example.com");
        existingUser.setUsername("oldusername");


        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(userId);
        updatedUserDTO.setEmail("newemail@example.com");
        updatedUserDTO.setUsername("newusername");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(modelMapper.map(existingUser, UserDTO.class)).thenReturn(updatedUserDTO);

        // Act
        UserDTO result = userService.updateUser(userId, userDTO);

        // Assert
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getUsername(), result.getUsername());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(userDTO.getEmail());
        verify(userRepository).existsByUsername(userDTO.getUsername());
        verify(userRepository).save(existingUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_SuccessfullyDelete() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("Ruslan");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_UserNotFound_UserNotFoundExceptionThrown() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }


}